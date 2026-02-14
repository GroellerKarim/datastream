package eu.groeller.ds.presentation.api;

import eu.groeller.ds.config.TestContainersConfig;
import eu.groeller.ds.domain.User;
import eu.groeller.ds.persistence.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfig.class)
class UserControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Test
        void getMe_WithValidToken_ShouldReturnUser() throws Exception {
                // Act & Assert – user will be auto-created from token claims
                mockMvc.perform(get("/api/v1/users/me")
                                .with(jwt().jwt(j -> j
                                                .subject("kc-user-123")
                                                .claim("preferred_username", "testuser")
                                                .claim("email", "test@example.com"))))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testuser"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
        }

        @Test
        void getMe_WithExistingUser_ShouldReturnExistingUser() throws Exception {
                // Arrange - pre-create user
                var user = new User("existinguser", "existing@example.com", "kc-existing-456");
                userRepository.save(user);

                // Act & Assert – should find existing user, not create a new one
                mockMvc.perform(get("/api/v1/users/me")
                                .with(jwt().jwt(j -> j
                                                .subject("kc-existing-456")
                                                .claim("preferred_username", "existinguser")
                                                .claim("email", "existing@example.com"))))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("existinguser"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("existing@example.com"));
        }

        @Test
        void getMe_WithoutToken_ShouldReturn401() throws Exception {
                mockMvc.perform(get("/api/v1/users/me"))
                                .andExpect(status().isUnauthorized());
        }
}