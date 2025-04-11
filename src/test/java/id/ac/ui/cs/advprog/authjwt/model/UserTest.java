package id.ac.ui.cs.advprog.authjwt.model;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

public class UserTest {

    @Test
    void testUserGettersAndSetters() {
        User user = new User();

        user.setUserId(UUID.randomUUID());
        user.setUsername("test@example.com");
        user.setPassword("password123");
        user.setRole("Admin");
        user.setNip("12345");
        user.setNim("67890");
        user.setFullName("John Doe");

        assertThat(user.getUserId()).isNotNull();
        assertThat(user.getUsername()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getRole()).isEqualTo("Admin");
        assertThat(user.getNip()).isEqualTo("12345");
        assertThat(user.getNim()).isEqualTo("67890");
        assertThat(user.getFullName()).isEqualTo("John Doe");
    }

    @Test
    void testUserConstructor() {
        String email = "test@example.com";
        String password = "password123";
        UUID id = UUID.randomUUID();

        User user = new User(id, email, password);

        assertThat(user.getUserId()).isNotNull();
        assertThat(user.getUsername()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void testUserConstructorWithWorkerTrueAndNumber() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String email = "test@domain.com";
        String password = "password123";
        String fullName = "John Doe";
        boolean worker = true;
        String number = "123456789";

        User user = new User(uuid, email, password, fullName, worker, number);

        assertEquals(uuid, user.getUserId());
        assertEquals(email, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(fullName, user.getFullName());
        assertEquals("LECTURER", user.getRole());
        assertEquals(number, user.getNip());
        assertNull(user.getNim());
    }

    @Test
    void testUserConstructorWithWorkerFalseAndNumber() {
        UUID uuid = UUID.randomUUID();
        String email = "student@domain.com";
        String password = "password123";
        String fullName = "Jane Doe";
        boolean worker = false;
        String number = "987654321";

        User user = new User(uuid, email, password, fullName, worker, number);

        assertEquals(uuid, user.getUserId());
        assertEquals(email, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(fullName, user.getFullName());
        assertEquals("STUDENT", user.getRole());
        assertNull(user.getNip());
        assertEquals(number, user.getNim());
    }
}
