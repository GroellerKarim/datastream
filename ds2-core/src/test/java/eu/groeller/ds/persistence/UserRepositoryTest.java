package eu.groeller.ds.persistence;

import eu.groeller.ds.config.TestContainersConfig;
import eu.groeller.ds.domain.User;
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
import java.util.UUID;

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
            UserEntity user = UserEntity.from(new User("user" + i, "user" + i + "@example.com", "kc-user-" + i));
            user.setPassword("password" + i);
            entityManager.persist(user);
        }
        entityManager.flush();
    }

    @Test
    void findById_WhenIdDoesNotExist_ReturnsEmptyOptional() {
        Optional<UserEntity> result = queryUserRepository.findById(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_WhenIdExists_ReturnsUser() {
        UserEntity user1 = UserEntity.from(new User("userZ", "user@example.com", "kc-user-z"));
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

    /**
     * The id is assigned by the domain before anything is written, so it no longer signals whether
     * a row exists. Saving must still insert, and the row must come back under exactly the id the
     * domain chose rather than one the database picked.
     */
    @Test
    void save_WhenIdWasAssignedByTheDomain_InsertsTheRow() {
        User domainUser = new User("assigned", "assigned@example.com", "kc-assigned");

        UserEntity saved = queryUserRepository.save(UserEntity.from(domainUser));
        entityManager.flush();
        entityManager.clear();

        assertEquals(domainUser.getId(), saved.getId());
        assertTrue(queryUserRepository.findById(domainUser.getId()).isPresent());
    }

    /** The other half of the same distinction: a row that has been read back must update, not re-insert. */
    @Test
    void save_WhenEntityWasLoaded_UpdatesInsteadOfInserting() {
        User domainUser = new User("loaded", "loaded@example.com", "kc-loaded");
        queryUserRepository.save(UserEntity.from(domainUser));
        entityManager.flush();
        entityManager.clear();

        UserEntity loaded = queryUserRepository.findById(domainUser.getId()).orElseThrow();
        loaded.setEmail("changed@example.com");
        queryUserRepository.save(loaded);
        entityManager.flush();
        entityManager.clear();

        assertEquals(1, queryUserRepository.findAll().stream()
                .filter(u -> u.getId().equals(domainUser.getId())).count());
        assertEquals("changed@example.com",
                queryUserRepository.findById(domainUser.getId()).orElseThrow().getEmail());
    }
}
