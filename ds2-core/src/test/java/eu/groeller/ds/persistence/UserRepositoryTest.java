package eu.groeller.ds.persistence;

import eu.groeller.ds.config.TestContainersConfig;
import eu.groeller.ds.persistence.entity.UserEntity;
import eu.groeller.ds.persistence.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestContainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    UserRepository queryUserRepository;

    @Autowired
    TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        for (int i = 1; i <= 10; i++) {
            UserEntity user = new UserEntity();
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@example.com");
            user.setPassword("password" + i);
            entityManager.persist(user);
        }
        entityManager.flush();
    }

    @Test
    void findById_WhenIdDoesNotExist_ReturnsEmptyOptional() {
        Optional<UserEntity> result = queryUserRepository.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_WhenIdExists_ReturnsUser() {
        UserEntity user1 = new UserEntity();
        user1.setUsername("userZ");
        user1.setEmail("user@example.com");
        user1.setPassword("password");
        user1 = entityManager.persist(user1);
        entityManager.flush();

        Optional<UserEntity> result = queryUserRepository.findById(user1.getId());

        assertTrue(result.isPresent());
        assertEquals(user1.getId(), result.get().getId());
        assertEquals(user1.getUsername(), result.get().getUsername());
        assertEquals(user1.getEmail(), result.get().getEmail());
    }

    @Test
    void findByUsername_WhenUsernameDoesNotExist_ReturnsEmptyOptional() {
        Optional<UserEntity> result = queryUserRepository.findByUsername("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUsername_WhenUsernameExists_ReturnsUser() {
        String username = "user1";
        UserEntity user = entityManager.getEntityManager()
                .createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity.class)
                .setParameter("username", username)
                .getSingleResult();

        Optional<UserEntity> result = queryUserRepository.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
        assertEquals(user.getUsername(), result.get().getUsername());
        assertEquals(user.getEmail(), result.get().getEmail());
    }

    @Test
    void findByEmail_WhenEmailDoesNotExist_ReturnsEmptyOptional() {
        Optional<UserEntity> result = queryUserRepository.findByEmail("nonexistent@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByEmail_WhenEmailExists_ReturnsUser() {
        String email = "user1@example.com";
        UserEntity user = entityManager.getEntityManager()
                .createQuery("SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class)
                .setParameter("email", email)
                .getSingleResult();

        Optional<UserEntity> result = queryUserRepository.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
        assertEquals(user.getUsername(), result.get().getUsername());
        assertEquals(user.getEmail(), result.get().getEmail());
    }

    @Test
    void existsByUsername_WhenUsernameDoesNotExist_ReturnsFalse() {
        boolean exists = queryUserRepository.existsByUsername("nonexistent");

        assertFalse(exists);
    }

    @Test
    void existsByUsername_WhenUsernameExists_ReturnsTrue() {
        String username = "user1";

        boolean exists = queryUserRepository.existsByUsername(username);

        assertTrue(exists);
    }

    @Test
    void existsByEmail_WhenEmailDoesNotExist_ReturnsFalse() {
        boolean exists = queryUserRepository.existsByEmail("nonexistent@example.com");

        assertFalse(exists);
    }

    @Test
    void existsByEmail_WhenEmailExists_ReturnsTrue() {
        String email = "user1@example.com";

        boolean exists = queryUserRepository.existsByEmail(email);

        assertTrue(exists);
    }
}
