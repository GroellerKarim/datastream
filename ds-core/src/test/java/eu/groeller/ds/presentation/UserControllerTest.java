package eu.groeller.ds.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.groeller.ds.configuration.SecuredWebMvcTest;
import eu.groeller.ds.persistence.user.UserRepository;
import eu.groeller.ds.presentation.api.UserController;
import eu.groeller.ds.presentation.request.user.UserLoginRequest;
import eu.groeller.ds.presentation.request.user.UserRequest;
import eu.groeller.ds.presentation.response.user.UserLoginResponse;
import eu.groeller.ds.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SecuredWebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    void createUser_ShouldReturnCreated() throws Exception {
        UserRequest request = new UserRequest("testuser", "testmail@test.com", "password123");

        mockMvc.perform(post(UserController.API_PATH + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(userService).createUser(any(UserRequest.class));
    }

    @Test
    @WithMockUser
    void login_ShouldReturnTokenAndUserId() throws Exception {
        UserLoginRequest request = new UserLoginRequest("testuser", "password123");

        UserLoginResponse response = new UserLoginResponse("testuser", "testemail@test.com", "test-token");
        when(userService.login(any(UserLoginRequest.class))).thenReturn(response);

        mockMvc.perform(post(UserController.API_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("testemail@test.com"))
                .andExpect(jsonPath("$.token").value("test-token"));
    }
}
