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
        String number = "123456789"; // NIP for Lecturer

        // Act
        User user = new User(uuid, email, password, fullName, worker, number);

        // Assert
        assertEquals(uuid, user.getUserId()); // Check that userId is set correctly
        assertEquals(email, user.getUsername()); // Check that username is set correctly
        assertEquals(password, user.getPassword()); // Check that password is set correctly
        assertEquals(fullName, user.getFullName()); // Check that fullName is set correctly
        assertEquals("LECTURER", user.getRole()); // Check that role is "LECTURER" when worker is true
        assertEquals(number, user.getNip()); // Check that nip is set correctly for Lecturer
        assertNull(user.getNim()); // Ensure nim is null for Lecturer
    }

    @Test
    void testUserConstructorWithWorkerFalseAndNumber() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String email = "student@domain.com";
        String password = "password123";
        String fullName = "Jane Doe";
        boolean worker = false;
        String number = "987654321"; // NIM for Student

        // Act
        User user = new User(uuid, email, password, fullName, worker, number);

        // Assert
        assertEquals(uuid, user.getUserId()); // Check that userId is set correctly
        assertEquals(email, user.getUsername()); // Check that username is set correctly
        assertEquals(password, user.getPassword()); // Check that password is set correctly
        assertEquals(fullName, user.getFullName()); // Check that fullName is set correctly
        assertEquals("STUDENT", user.getRole()); // Check that role is "STUDENT" when worker is false
        assertNull(user.getNip()); // Ensure nip is null for Student
        assertEquals(number, user.getNim()); // Check that nim is set correctly for Student
    }
}
