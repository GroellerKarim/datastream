package eu.groeller.datastreamserver.presentation.api;

import eu.groeller.datastreamserver.presentation.request.user.UserLoginRequest;
import eu.groeller.datastreamserver.presentation.request.user.UserRequest;
import eu.groeller.datastreamserver.presentation.response.user.UserLoginResponse;
import eu.groeller.datastreamserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(UserController.API_PATH)
public class UserController {
    
    public static final String API_PATH = "/api/v1/users";
    
    private final UserService userService;

    @PostMapping("/register")
    public HttpEntity<String> createUser(@RequestBody UserRequest userRequest) {
        userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("User was successfully registered");
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }
}
