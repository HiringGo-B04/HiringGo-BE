package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.authjwt.service.command.AdminRegistrationCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminRegistrationCommandTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminRegistrationCommand adminRegistrationCommand;

    private User validUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup a valid user
        validUser = new User();
        validUser.setUsername("admin@gmail.com");
        validUser.setPassword("password");
    }

    @Test
    void testAddUser_Success() {
        User user = new User(UUID.randomUUID(), "admin@gmail.com", "password", "Admin User", true, "12345678");

        when(userRepository.existsByUsername("admin@gmail.com")).thenReturn(false);  // Simulate the username doesn't exist
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");  // Simulate encoding the password

        AdminRegistrationCommand adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, user);
        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("accept", response.get("status"));
        assertEquals("Success register", response.get("messages"));
        assertEquals("admin@gmail.com", response.get("username"));
        assertEquals("ADMIN", response.get("role"));

        verify(userRepository).existsByUsername("admin@gmail.com");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }


    @Test
    void testAddUser_InvalidPayload() {
        User invalidUser = new User();
        invalidUser.setUsername("");
        invalidUser.setPassword("");
        invalidUser.setFullName("");
        invalidUser.setNip("");

        AdminRegistrationCommand invalidCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, invalidUser);
        ResponseEntity<Map<String, String>> responseEntity = invalidCommand.addUser();
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_InvalidPayload_EmptyUsername() {
        validUser.setUsername("");
        validUser.setPassword("password");

        adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, validUser);
        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_InvalidPayload_EmptyPassword() {
        validUser.setUsername("admin@gmail.com");
        validUser.setPassword("");

        adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, validUser);
        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_UsernameAlreadyExists() {
        User user = new User(UUID.randomUUID(), "admin@gmail.com", "password", "Admin User", true, "12345678");

        when(userRepository.existsByUsername("admin@gmail.com")).thenReturn(true);
        AdminRegistrationCommand adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, user);
        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Username already exists", response.get("message"));

        verify(userRepository).existsByUsername("admin@gmail.com");
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void testAddUser_Exception() {
        User user = new User(UUID.randomUUID(), "admin@gmail.com", "password", "Admin User", true, "12345678");

        when(userRepository.existsByUsername("admin@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        doThrow(new RuntimeException("Database error")).when(userRepository).save(any(User.class));

        AdminRegistrationCommand adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, user);
        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Database error", response.get("messages"));

        verify(userRepository).existsByUsername("admin@gmail.com");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }

    @Test
    void testAddUser_Admin_NullPassword() {
        User invalidUser = new User(UUID.randomUUID(), "admin@gmail.com", null);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_Admin_EmptyPassword() {
        User invalidUser = new User(UUID.randomUUID(), "admin@gmail.com", "");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_Admin_NullUsername() {
        User invalidUser = new User(UUID.randomUUID(), null, "adminPassword");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_Admin_InvalidEmailFormat() {
        // Use an invalid email format
        User invalidUser = new User(UUID.randomUUID(), "invalid-email", "adminPassword");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Username must be a valid email address", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }




}
