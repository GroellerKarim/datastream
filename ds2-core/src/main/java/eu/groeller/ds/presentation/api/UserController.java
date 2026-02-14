package eu.groeller.ds.presentation.api;

import eu.groeller.ds.domain.User;
import eu.groeller.ds.presentation.response.user.UserTokenLoginResponse;
import eu.groeller.ds.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(UserController.API_PATH)
public class UserController {

    public static final String API_PATH = "/api/v1/users";

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserTokenLoginResponse> getCurrentUser(JwtAuthenticationToken authToken) {
        User user = userService.resolveUser(authToken);
        return ResponseEntity.ok(new UserTokenLoginResponse(user.getUsername(), user.getEmail()));
    }
}
