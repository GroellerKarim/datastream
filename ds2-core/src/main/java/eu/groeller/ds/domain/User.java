package eu.groeller.ds.domain;

import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

@Getter
public class User implements UserData {

    private final UUID id;
    private final String username;
    private final String email;
    private final String keycloakId;

    public User(@NonNull String username, @NonNull String email, @NonNull String keycloakId) {
        this(UUID.randomUUID(), username, email, keycloakId);
    }

    private User(@NonNull UUID id, @NonNull String username, @NonNull String email, @NonNull String keycloakId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.keycloakId = keycloakId;
    }

    public static User loadFromExisting(@NonNull UserData data) {
        return new User(data.getId(), data.getUsername(), data.getEmail(), data.getKeycloakId());
    }
}
