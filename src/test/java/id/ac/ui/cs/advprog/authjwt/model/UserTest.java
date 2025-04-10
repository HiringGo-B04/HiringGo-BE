package id.ac.ui.cs.advprog.authjwt.model;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @Test
    void testUserGettersAndSetters() {
        User user = new User();

        user.setUserId(UUID.randomUUID()); // Manually setting a UUID
        user.setUsername("test@example.com");
        user.setPassword("password123");
        user.setRole("Admin");
        user.setNip("12345");
        user.setNim("67890");
        user.setFullName("John Doe");

        assertThat(user.getUserId()).isNotNull(); // Check if UUID is generated (it's random so we just check it's not null)
        assertThat(user.getUsername()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getRole()).isEqualTo("Admin");
        assertThat(user.getNip()).isEqualTo("12345");
        assertThat(user.getNim()).isEqualTo("67890");
        assertThat(user.getFullName()).isEqualTo("John Doe");
    }
}
