package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.authjwt.service.command.StudentRegistrationCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentRegistrationCommandTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private StudentRegistrationCommand studentRegistrationCommand;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a dummy user for testing
        user = new User();
        user.setUsername("student1");
        user.setPassword("password");
        user.setFullName("Student One");
        user.setNim("12345678");

        // Initialize the command with mocks
        studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, user);
    }

    @Test
    void testAddUser_Success() {
        // Mock behavior for userRepository
        when(userRepository.existsByUsername("student1")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Mock the save behavior to return the saved user
        User newUser = new User(UUID.randomUUID(), "student1", "encodedPassword", "Student One", false, "12345678");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("accept", response.get("status"));
        assertEquals("Success register", response.get("messages"));
        assertEquals("student1", response.get("username"));
        assertEquals("STUDENT", response.get("role"));

        // Verify interactions
        verify(userRepository).existsByUsername("student1");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }

    @Test
    void testAddUser_InvalidPayload() {
        // Create a user with missing information
        User invalidUser = new User();
        invalidUser.setUsername(""); // Empty username
        invalidUser.setPassword(""); // Empty password
        invalidUser.setFullName(""); // Empty full name
        invalidUser.setNim(""); // Empty NIM

        // Re-initialize the command with the invalid user
        studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_UsernameAlreadyExists() {
        // Mock behavior for an existing username
        when(userRepository.existsByUsername("student1")).thenReturn(true);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Username already exists", response.get("message"));
    }

    @Test
    void testAddUser_Exception() {
        // Mock behavior for a general exception
        when(userRepository.existsByUsername("student1")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Database error", response.get("messages"));
    }

    @Test
    void testAddUser_InvalidUsername() {
        // Create a user with empty username
        User invalidUser = new User();
        invalidUser.setUsername(""); // Empty username
        invalidUser.setPassword("password");
        invalidUser.setFullName("Student One");
        invalidUser.setNim("12345678");

        // Re-initialize the command with the invalid user
        studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_InvalidPassword() {
        // Create a user with empty password
        User invalidUser = new User();
        invalidUser.setUsername("student1");
        invalidUser.setPassword(""); // Empty password
        invalidUser.setFullName("Student One");
        invalidUser.setNim("12345678");

        // Re-initialize the command with the invalid user
        studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_InvalidFullName() {
        // Create a user with empty full name
        User invalidUser = new User();
        invalidUser.setUsername("student1");
        invalidUser.setPassword("password");
        invalidUser.setFullName(""); // Empty full name
        invalidUser.setNim("12345678");

        // Re-initialize the command with the invalid user
        studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_InvalidNim() {
        // Create a user with empty NIM
        User invalidUser = new User();
        invalidUser.setUsername("student1");
        invalidUser.setPassword("password");
        invalidUser.setFullName("Student One");
        invalidUser.setNim(""); // Empty NIM

        // Re-initialize the command with the invalid user
        studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_Student_EmptyFields() {
        // Arrange: Create a student with empty properties
        User invalidUser = new User(UUID.randomUUID(), "", "", "", false, "");  // All required fields are empty strings

        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);  // Simulate that the username doesn't exist

        // Initialize the StudentRegistrationCommand with the mock user
        StudentRegistrationCommand studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());  // The status should be 403 for invalid payload
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);  // Ensure the response body is not null
        assertEquals("error", response.get("status"));  // Assert the error status
        assertEquals("Invalid payload", response.get("message"));  // Assert the error message

        // Verify interactions
        verify(userRepository, never()).save(any(User.class));  // Ensure save() was never called, as the input is invalid
    }

    @Test
    void testAddUser_Student_NullPassword() {
        // Arrange: Create a student with null password
        User invalidUser = new User(UUID.randomUUID(), "student1", null, "Student One", false, "12345678");  // Password is null

        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);  // Simulate that the username doesn't exist

        // Initialize the StudentRegistrationCommand with the mock user
        StudentRegistrationCommand studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());  // The status should be 403 for invalid payload
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);  // Ensure the response body is not null
        assertEquals("error", response.get("status"));  // Assert the error status
        assertEquals("Invalid payload", response.get("message"));  // Assert the error message

        // Verify interactions
        verify(userRepository, never()).save(any(User.class));  // Ensure save() was never called, as the input is invalid
    }

    @Test
    void testAddUser_Student_NullFullname() {
        // Arrange: Create a student with null fullname
        User invalidUser = new User(UUID.randomUUID(), "student1", "aksj", null, false, "12345678");  // Fullname is null

        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);  // Simulate that the username doesn't exist

        // Initialize the StudentRegistrationCommand with the mock user
        StudentRegistrationCommand studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());  // The status should be 403 for invalid payload
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);  // Ensure the response body is not null
        assertEquals("error", response.get("status"));  // Assert the error status
        assertEquals("Invalid payload", response.get("message"));  // Assert the error message

        // Verify interactions
        verify(userRepository, never()).save(any(User.class));  // Ensure save() was never called, as the input is invalid
    }

    @Test
    void testAddUser_Student_NullNIP() {
        // Arrange: Create a student with null NIP
        User invalidUser = new User(UUID.randomUUID(), "student1", "aaa", "Student One", false, null);  // NIP is null

        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);  // Simulate that the username doesn't exist

        // Initialize the StudentRegistrationCommand with the mock user
        StudentRegistrationCommand studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

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
