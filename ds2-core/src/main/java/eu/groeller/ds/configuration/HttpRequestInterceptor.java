package eu.groeller.ds.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Configuration
public class HttpRequestInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());

        // Log URL and query parameters
        String queryString = request.getQueryString();
        String url = request.getRequestURL() + (queryString != null ? "?" + queryString : "");
        log.info("Incoming request: {} {}", request.getMethod(), url);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long duration = System.currentTimeMillis() - startTime;

        // Get URL with query parameters
        String queryString = request.getQueryString();
        String url = request.getRequestURL() + (queryString != null ? "?" + queryString : "");

        log.info("Request completed - URL: {}, Duration: {}ms, Status: {}", url, duration, response.getStatus());
    }
}