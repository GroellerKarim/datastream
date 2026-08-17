package eu.groeller.ds.persistence.entity;

import eu.groeller.ds.domain.UserData;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Hibernate only; every other path must supply the domain-assigned id
@Entity
@Table(name = "users")
public class UserEntity extends AbstractJpaEntity implements UserData {

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "keycloak_id", unique = true)
    private String keycloakId;

    public UserEntity(@NonNull UUID id, String username, String email, String keycloakId) {
        super(id);
        this.username = username;
        this.email = email;
        this.keycloakId = keycloakId;
    }

    /** Takes the identity from the domain object rather than waiting for the database to mint one. */
    public static UserEntity from(@NonNull UserData user) {
        return new UserEntity(user.getId(), user.getUsername(), user.getEmail(), user.getKeycloakId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(username, that.username)
                && Objects.equals(email, that.email)
                && Objects.equals(keycloakId, that.keycloakId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, keycloakId);
    }
}
