package eu.groeller.ds.presentation.api;

import eu.groeller.ds.config.TestContainersConfig;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "GET /v3/api-docs",
            "GET /v3/api-docs/swagger-config",
            "GET /v3/api-docs.yaml",
            "GET /swagger-ui.html",
            "GET /swagger-ui/**",
            "POST /api/v1/users/register",
            "POST /api/v1/users/login"
    );

    protected Stream<EndpointInfo> provideEndpoints() {
        List<EndpointInfo> endpoints = new ArrayList<>();

        // Find all classes annotated with @RestController or @Controller across all packages
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(Scanners.TypesAnnotated));

        Set<Class<?>> controllers = new HashSet<>();
        controllers.addAll(reflections.getTypesAnnotatedWith(RestController.class));
        controllers.addAll(reflections.getTypesAnnotatedWith(Controller.class));

        handlerMapping.getHandlerMethods().forEach((mapping, method) -> {
            // Include all endpoints, not just those from our controllers
            Set<RequestMethod> methods = mapping.getMethodsCondition().getMethods();
            Set<String> patterns = mapping.getPatternValues();

            patterns.forEach(pattern -> {
                // If no methods are specified, assume it accepts GET
                if (methods.isEmpty()) {
                    String endpoint = "GET " + pattern;
                    if (!PUBLIC_ENDPOINTS.contains(endpoint) && !isPatternMatchingPublicEndpoint(endpoint)) {
                        endpoints.add(new EndpointInfo(pattern, RequestMethod.GET));
                    }
                } else {
                    methods.forEach(requestMethod -> {
                        String endpoint = requestMethod + " " + pattern;
                        if (!PUBLIC_ENDPOINTS.contains(endpoint) && !isPatternMatchingPublicEndpoint(endpoint)) {
                            endpoints.add(new EndpointInfo(pattern, requestMethod));
                        }
                    });
                }
            });
        });

        return endpoints.stream();
    }

    private boolean isPatternMatchingPublicEndpoint(String endpoint) {
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(publicPattern -> {
                    // Split into method and path
                    String[] endpointParts = endpoint.split(" ", 2);
                    String[] patternParts = publicPattern.split(" ", 2);

                    // Methods must match exactly
                    if (!endpointParts[0].equals(patternParts[0])) {
                        return false;
                    }

                    // Convert pattern to regex
                    String patternRegex = patternParts[1]
                            .replace("/**", "/.*")  // /** matches anything including slashes
                            .replace("/*", "/[^/]*")  // /* matches anything except slashes
                            .replace(".", "\\.")
                            .replace("?", "\\?");

                    return endpointParts[1].matches("^" + patternRegex + "$");
                });
    }

    @ParameterizedTest()
    @MethodSource("provideEndpoints")
    void endpoint_WhenNotAuthenticated_ShouldReturn401(EndpointInfo endpointInfo) throws Exception {
        String pattern = replacePathVariables(endpointInfo.pattern());
        String endpoint = endpointInfo.method() + " " + pattern;
        System.out.println("Testing endpoint: " + endpoint);

        MockHttpServletRequestBuilder requestBuilder = switch (endpointInfo.method()) {
            case GET -> org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .get(pattern);
            case POST -> org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .post(pattern)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}");
            case PUT -> org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .put(pattern)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}");
            case DELETE -> org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .delete(pattern);
            case PATCH -> org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .patch(pattern)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}");
            default -> throw new IllegalStateException("Unexpected value: " + endpointInfo.method());
        };

        try {
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isUnauthorized());
        } catch (AssertionError e) {
            System.err.println("Endpoint " + endpoint + " returned wrong status code");
            throw e;
        }
    }

    protected Stream<EndpointInfo> providePublicEndpoints() {
        List<EndpointInfo> endpoints = new ArrayList<>();

        handlerMapping.getHandlerMethods().forEach((mapping, method) -> {
            Set<RequestMethod> methods = mapping.getMethodsCondition().getMethods();
            Set<String> patterns = mapping.getPatternValues();

            patterns.forEach(pattern -> {
                if (methods.isEmpty()) {
                    String endpoint = "GET " + pattern;
                    if (PUBLIC_ENDPOINTS.contains(endpoint) || isPatternMatchingPublicEndpoint(endpoint)) {
                        endpoints.add(new EndpointInfo(pattern, RequestMethod.GET));
                        System.out.println("Found public endpoint: " + endpoint);
                    }
                } else {
                    methods.forEach(requestMethod -> {
                        String endpoint = requestMethod + " " + pattern;
                        if (PUBLIC_ENDPOINTS.contains(endpoint) || isPatternMatchingPublicEndpoint(endpoint)) {
                            endpoints.add(new EndpointInfo(pattern, requestMethod));
                            System.out.println("Found public endpoint: " + endpoint);
                        }
                    });
                }
            });
        });

        return endpoints.stream();
    }

    @ParameterizedTest()
    @MethodSource("providePublicEndpoints")
    void publicEndpoint_WhenNotAuthenticated_ShouldNotReturn401(EndpointInfo endpointInfo) throws Exception {
        String pattern = replacePathVariables(endpointInfo.pattern());
        String endpoint = endpointInfo.method() + " " + pattern;
        System.out.println("Testing public endpoint: " + endpoint);

        MockHttpServletRequestBuilder requestBuilder = switch (endpointInfo.method()) {
            case GET -> org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .get(pattern);
            case POST -> org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .post(pattern)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(switch (pattern) {
                        case "/api/v1/users/register" -> """
                                {
                                    "username": "testuser",
                                    "email": "test@example.com",
                                    "password": "password123"
                                }
                                """;
                        case "/api/v1/users/login" -> """
                                {
                                    "email": "test@example.com",
                                    "password": "password123"
                                }
                                """;
                        default -> "{}";
                    });
            case PUT -> org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .put(pattern)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}");
            case DELETE -> org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .delete(pattern);
            case PATCH -> org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .patch(pattern)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}");
            default -> throw new IllegalStateException("Unexpected value: " + endpointInfo.method());
        };

        try {
            mockMvc.perform(requestBuilder)
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        if (status == 401) {
                            throw new AssertionError("Public endpoint " + endpoint + " returned 401 Unauthorized");
                        }
                    });
        } catch (AssertionError e) {
            System.err.println("Public endpoint " + endpoint + " returned wrong status code");
            throw e;
        }
    }

    private String replacePathVariables(String pattern) {
        Pattern pathVarPattern = Pattern.compile("\\{([^}]+)}");
        Matcher matcher = pathVarPattern.matcher(pattern);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String varName = matcher.group(1);
            // Replace path variables with dummy values based on their name
            String replacement = switch (varName) {
                case "id", "workoutId", "workoutTypeId", "exerciseId" -> "1";
                default -> "test";
            };
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    protected record EndpointInfo(String pattern, RequestMethod method) {
        @Override
        public String toString() {
            return method + " " + pattern;
        }
    }
} 