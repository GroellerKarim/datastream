package eu.groeller.ds.domain;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class User implements UserData {

    private final String username;
    private final String email;
    private final String keycloakId;

    public User(@NonNull String username, @NonNull String email, @NonNull String keycloakId) {
        this.username = username;
        this.email = email;
        this.keycloakId = keycloakId;
    }

    public static User loadFromExisting(@NonNull UserData data) {
        return new User(data.getUsername(), data.getEmail(), data.getKeycloakId());
    }
}
