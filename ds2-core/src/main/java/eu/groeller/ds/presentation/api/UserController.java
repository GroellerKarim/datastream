package eu.groeller.ds.presentation.api;

import eu.groeller.ds.configuration.security.CustomUserDetails;
import eu.groeller.ds.presentation.request.user.UserLoginRequest;
import eu.groeller.ds.presentation.request.user.UserRequest;
import eu.groeller.ds.presentation.response.user.UserLoginResponse;
import eu.groeller.ds.presentation.response.user.UserTokenLoginResponse;
import eu.groeller.ds.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/token")
    public ResponseEntity<UserTokenLoginResponse> getUserWithToken(@AuthenticationPrincipal CustomUserDetails authPrincipal) {
        val user = authPrincipal.getUser();
        return ResponseEntity.ok(new UserTokenLoginResponse(user.getUsername(), user.getEmail()));
    }

}
