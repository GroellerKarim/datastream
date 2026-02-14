package eu.groeller.ds.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "users")
public class User extends AbstractEntity {

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "keycloak_id", unique = true)
    private String keycloakId;

    public User(String username, String email, String keycloakId) {
        this.username = username;
        this.email = email;
        this.keycloakId = keycloakId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(email, user.email)
                && Objects.equals(keycloakId, user.keycloakId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, email, keycloakId);
    }
}
