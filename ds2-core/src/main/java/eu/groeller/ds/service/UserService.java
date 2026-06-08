package eu.groeller.ds.service;

import eu.groeller.ds.persistence.entity.UserEntity;
import eu.groeller.ds.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserEntity resolveUser(JwtAuthenticationToken authToken) {
        String keycloakId = authToken.getToken().getSubject();

        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    String username = authToken.getToken().getClaimAsString("preferred_username");
                    String email = authToken.getToken().getClaimAsString("email");

                    if (username == null || username.isBlank()) {
                        username = keycloakId;
                    }
                    if (email == null || email.isBlank()) {
                        email = username + "@keycloak.local";
                    }

                    var user = new UserEntity(username, email, keycloakId);
                    user = userRepository.save(user);
                    log.info("Auto-created local user [{}] from Keycloak subject [{}]", user.getUsername(), keycloakId);
                    return user;
                });
    }
}
