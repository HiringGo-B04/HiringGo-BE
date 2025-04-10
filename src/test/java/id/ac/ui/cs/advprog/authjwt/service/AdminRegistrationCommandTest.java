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

import java.util.HashMap;
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
        validUser.setUsername("admin");
        validUser.setPassword("password");
    }

    @Test
    void testAddUser_Success() {
        // Arrange: Create a valid user
        User user = new User(UUID.randomUUID(), "admin", "password", "Admin User", true, "12345678");

        // Mock dependencies
        when(userRepository.existsByUsername("admin")).thenReturn(false);  // Simulate the username doesn't exist
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");  // Simulate encoding the password
        // No need to mock save() because it's a void method. Just verify it's called.

        // Initialize the AdminRegistrationCommand with the mock user
        AdminRegistrationCommand adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, user);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());  // Assert the status code
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);  // Ensure the response body is not null
        assertEquals("accept", response.get("status"));  // Check the status in the response
        assertEquals("Success register", response.get("messages"));  // Check the message
        assertEquals("admin", response.get("username"));  // Assert the correct username
        assertEquals("ADMIN", response.get("role"));  // Assert the role is "ADMIN"

        // Verify interactions: Ensure methods are called with correct parameters
        verify(userRepository).existsByUsername("admin");  // Verify that existsByUsername was called
        verify(userRepository).save(any(User.class));  // Verify that save() was called
        verify(passwordEncoder).encode("password");  // Verify that encode() was called
    }


    @Test
    void testAddUser_InvalidPayload() {
        // Invalid user (empty username and password)
        User invalidUser = new User();
        invalidUser.setUsername("");
        invalidUser.setPassword("");
        invalidUser.setFullName("");
        invalidUser.setNip("");

        AdminRegistrationCommand invalidCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = invalidCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_InvalidPayload_EmptyUsername() {
        // Arrange
        validUser.setUsername("");  // Empty username
        validUser.setPassword("password");  // Valid password

        adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, validUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        // Verify that no interactions with the repository occurred
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_InvalidPayload_EmptyPassword() {
        // Arrange
        validUser.setUsername("admin");  // Valid username
        validUser.setPassword("");  // Empty password

        adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, validUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        // Verify that no interactions with the repository occurred
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_UsernameAlreadyExists() {
        // Arrange: Create a valid user
        User user = new User(UUID.randomUUID(), "admin", "password", "Admin User", true, "12345678");

        // Mock dependencies
        when(userRepository.existsByUsername("admin")).thenReturn(true);  // Simulate the username exists

        // Initialize the AdminRegistrationCommand with the mock user
        AdminRegistrationCommand adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, user);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());  // Assert the status code
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);  // Ensure the response body is not null
        assertEquals("error", response.get("status"));  // Check the error status
        assertEquals("Username already exists", response.get("message"));  // Assert the message is correct

        // Verify interactions: Ensure methods are called with correct parameters
        verify(userRepository).existsByUsername("admin");  // Verify that existsByUsername was called
        verify(userRepository, never()).save(any(User.class));  // Ensure save() was never called
    }


    @Test
    void testAddUser_Exception() {
        // Arrange: Create a valid user
        User user = new User(UUID.randomUUID(), "admin", "password", "Admin User", true, "12345678");

        // Mock dependencies
        when(userRepository.existsByUsername("admin")).thenReturn(false);  // Simulate that the username doesn't exist
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");  // Simulate password encoding
        doThrow(new RuntimeException("Database error")).when(userRepository).save(any(User.class));  // Simulate a database error on save

        // Initialize the AdminRegistrationCommand with the mock user
        AdminRegistrationCommand adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, user);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());  // Assert the status code
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);  // Ensure the response body is not null
        assertEquals("error", response.get("status"));  // Assert the error status
        assertEquals("Database error", response.get("messages"));  // Assert the error message

        // Verify interactions: Ensure methods are called with correct parameters
        verify(userRepository).existsByUsername("admin");  // Verify that existsByUsername was called
        verify(userRepository).save(any(User.class));  // Ensure save() was called
        verify(passwordEncoder).encode("password");  // Ensure encode() was called
    }

    // Test Case 1: Password is null
    @Test
    void testAddUser_Admin_NullPassword() {
        // Arrange: Create a user with null password
        User invalidUser = new User(UUID.randomUUID(), "admin1", null);  // Password is null

        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);  // Simulate that the username doesn't exist

        // Initialize the AdminRegistrationCommand with the mock user
        adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());  // The status should be 403 for invalid payload
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);  // Ensure the response body is not null
        assertEquals("error", response.get("status"));  // Assert the error status
        assertEquals("Invalid payload", response.get("message"));  // Assert the error message

        // Verify interactions
        verify(userRepository, never()).save(any(User.class));  // Ensure save() was never called, as the input is invalid
    }

    // Test Case 2: Password is empty
    @Test
    void testAddUser_Admin_EmptyPassword() {
        // Arrange: Create a user with empty password
        User invalidUser = new User(UUID.randomUUID(), "admin1", "");  // Password is empty

        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);  // Simulate that the username doesn't exist

        // Initialize the AdminRegistrationCommand with the mock user
        adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());  // The status should be 403 for invalid payload
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);  // Ensure the response body is not null
        assertEquals("error", response.get("status"));  // Assert the error status
        assertEquals("Invalid payload", response.get("message"));  // Assert the error message

        // Verify interactions
        verify(userRepository, never()).save(any(User.class));  // Ensure save() was never called, as the input is invalid
    }

    // Test Case 3: Username is null
    @Test
    void testAddUser_Admin_NullUsername() {
        // Arrange: Create a user with null username
        User invalidUser = new User(UUID.randomUUID(), null, "adminPassword");  // Username is null

        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);  // Simulate that the username doesn't exist

        // Initialize the AdminRegistrationCommand with the mock user
        adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());  // The status should be 403 for invalid payload
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);  // Ensure the response body is not null
        assertEquals("error", response.get("status"));  // Assert the error status
        assertEquals("Invalid payload", response.get("message"));  // Assert the error message

        // Verify interactions
        verify(userRepository, never()).save(any(User.class));  // Ensure save() was never called, as the input is invalid
    }

    // Test Case 4: Username is empty
    @Test
    void testAddUser_Admin_EmptyUsername() {
        // Arrange: Create a user with empty username
        User invalidUser = new User(UUID.randomUUID(), "", "adminPassword");  // Username is empty

        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);  // Simulate that the username doesn't exist

        // Initialize the AdminRegistrationCommand with the mock user
        adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = adminRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());  // The status should be 403 for invalid payload
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);  // Ensure the response body is not null
        assertEquals("error", response.get("status"));  // Assert the error status
        assertEquals("Invalid payload", response.get("message"));  // Assert the error message

        // Verify interactions
        verify(userRepository, never()).save(any(User.class));  // Ensure save() was never called, as the input is invalid
    }
}
