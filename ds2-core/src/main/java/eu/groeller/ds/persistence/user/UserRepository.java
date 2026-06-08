package eu.groeller.ds.persistence.user;

import eu.groeller.ds.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByKeycloakId(String keycloakId);

    boolean existsByUsername(String username);

    boolean existsByEmail(@NonNull String email);
}
