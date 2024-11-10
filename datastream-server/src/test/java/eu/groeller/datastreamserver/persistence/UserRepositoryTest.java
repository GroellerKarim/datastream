package eu.groeller.datastreamserver.persistence;

import eu.groeller.datastreamserver.domain.User;
import eu.groeller.datastreamserver.persistence.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {
    
    @Autowired
    UserRepository queryUserRepository;

    @Autowired
    TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@example.com");
            user.setPassword("password" + i);
            entityManager.persist(user);
        }
        entityManager.flush();
    }

    @Test
    void findById_WhenIdDoesNotExist_ReturnsEmptyOptional() {
        Optional<User> result = queryUserRepository.findById(999L);
        
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_WhenIdExists_ReturnsUser() {
        User user1 = new User();
        user1.setUsername("userZ");
        user1.setEmail("user@example.com");
        user1.setPassword("password");
        user1 = entityManager.persist(user1);
        entityManager.flush();

        Optional<User> result = queryUserRepository.findById(user1.getId());
        
        assertTrue(result.isPresent());
        assertEquals(user1.getId(), result.get().getId());
        assertEquals(user1.getUsername(), result.get().getUsername());
        assertEquals(user1.getEmail(), result.get().getEmail());
    }

    @Test
    void findByUsername_WhenUsernameDoesNotExist_ReturnsEmptyOptional() {
        Optional<User> result = queryUserRepository.findByUsername("nonexistent");
        
        assertTrue(result.isEmpty());
    }

    @Test
    void findByUsername_WhenUsernameExists_ReturnsUser() {
        String username = "user1";
        User user = entityManager.getEntityManager().createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();
        
        Optional<User> result = queryUserRepository.findByUsername(username);
        
        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
        assertEquals(user.getUsername(), result.get().getUsername());
        assertEquals(user.getEmail(), result.get().getEmail());
    }

    @Test
    void findByEmail_WhenEmailDoesNotExist_ReturnsEmptyOptional() {
        Optional<User> result = queryUserRepository.findByEmail("nonexistent@example.com");
        
        assertTrue(result.isEmpty());
    }

    @Test
    void findByEmail_WhenEmailExists_ReturnsUser() {
        String email = "user1@example.com";
        User user = entityManager.getEntityManager().createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
        
        Optional<User> result = queryUserRepository.findByEmail(email);
        
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
