package eu.groeller.ds.service;

import eu.groeller.ds.domain.User;
import eu.groeller.ds.persistence.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    UserRepository mockUserRepository;
    UserService userService;

    @BeforeEach
    void setUp() {
        mockUserRepository = mock(UserRepository.class);
        userService = new UserService(mockUserRepository);
    }

    private JwtAuthenticationToken createMockAuthToken(String subject, String username, String email) {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .subject(subject)
                .claim("preferred_username", username)
                .claim("email", email)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        return new JwtAuthenticationToken(jwt);
    }

    @Test
    void resolveUser_WhenUserExists_ShouldReturnExistingUser() {
        var existingUser = new User("testuser", "test@example.com", "kc-123");
        when(mockUserRepository.findByKeycloakId("kc-123")).thenReturn(Optional.of(existingUser));

        var authToken = createMockAuthToken("kc-123", "testuser", "test@example.com");
        var result = userService.resolveUser(authToken);

        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("kc-123", result.getKeycloakId());
        verify(mockUserRepository, never()).save(any());
    }

    @Test
    void resolveUser_WhenUserDoesNotExist_ShouldCreateNewUser() {
        when(mockUserRepository.findByKeycloakId("kc-new-456")).thenReturn(Optional.empty());
        when(mockUserRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var authToken = createMockAuthToken("kc-new-456", "newuser", "new@example.com");
        var result = userService.resolveUser(authToken);

        assertEquals("newuser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("kc-new-456", result.getKeycloakId());
        verify(mockUserRepository).save(any(User.class));
    }

    @Test
    void resolveUser_WhenUsernameIsMissing_ShouldFallbackToSubject() {
        when(mockUserRepository.findByKeycloakId("kc-no-username")).thenReturn(Optional.empty());
        when(mockUserRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var authToken = createMockAuthToken("kc-no-username", null, "fallback@example.com");
        var result = userService.resolveUser(authToken);

        assertEquals("kc-no-username", result.getUsername());
        assertEquals("fallback@example.com", result.getEmail());
        verify(mockUserRepository).save(any(User.class));
    }

    @Test
    void resolveUser_WhenEmailIsMissing_ShouldGenerateFallbackEmail() {
        when(mockUserRepository.findByKeycloakId("kc-no-email")).thenReturn(Optional.empty());
        when(mockUserRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var authToken = createMockAuthToken("kc-no-email", "someuser", null);
        var result = userService.resolveUser(authToken);

        assertEquals("someuser", result.getUsername());
        assertEquals("someuser@keycloak.local", result.getEmail());
        verify(mockUserRepository).save(any(User.class));
    }
}
