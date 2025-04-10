package id.ac.ui.cs.advprog.authjwt.repository;

import id.ac.ui.cs.advprog.authjwt.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(UUID.randomUUID(), "test@example.com", "test@test.com");
        userRepository.save(user);
    }

    @Test
    void testFindByUsername() {
        User foundUser = userRepository.findByUsername("test@example.com");
        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getUsername());
    }

    @Test
    void testExistsByUsername() {
        boolean exists = userRepository.existsByUsername("test@example.com");
        assertTrue(exists);

        boolean notExists = userRepository.existsByUsername("doesnotexist@example.com");
        assertFalse(notExists);
    }
}
