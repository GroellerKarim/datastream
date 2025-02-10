package eu.groeller.ds.presentation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.groeller.ds.config.TestContainersConfig;
import eu.groeller.ds.presentation.request.user.UserLoginRequest;
import eu.groeller.ds.presentation.request.user.UserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfig.class)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_ShouldCreateNewUser() throws Exception {
        // Arrange
        String uniqueId = java.util.UUID.randomUUID().toString();
        UserRequest request = new UserRequest(
                "testuser_" + uniqueId,
                "test_" + uniqueId + "@user.com",
                "password123"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("User was successfully registered"));
    }

    @Test
    void login_ShouldReturnTokenForValidCredentials() throws Exception {
        // Arrange
        // First create a user
        UserRequest createRequest = new UserRequest("loginuser", "login@example.com", "password123");

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        // Create login request
        UserLoginRequest loginRequest = new UserLoginRequest("login@example.com", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("loginuser"));
    }

    @Test
    void login_ShouldReturnBadRequestForInvalidCredentials() throws Exception {
        // Arrange
        UserLoginRequest loginRequest = new UserLoginRequest("nonexistent@example.com", "wrongpassword");

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }
} 