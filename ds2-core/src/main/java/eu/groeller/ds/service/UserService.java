package eu.groeller.ds.service;

import eu.groeller.ds.domain.User;
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

                    var user = new User(username, email, keycloakId);
                    var entity = userRepository.save(UserEntity.from(user));
                    log.info("Auto-created local user [{}] with id [{}] from Keycloak subject [{}]",
                            entity.getUsername(), entity.getId(), keycloakId);
                    return entity;
                });
    }
}
