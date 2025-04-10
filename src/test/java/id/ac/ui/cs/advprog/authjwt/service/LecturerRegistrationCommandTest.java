package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.authjwt.service.command.LecturerRegistrationCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Map;
import java.util.UUID;

class LecturerRegistrationCommandTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private LecturerRegistrationCommand lecturerRegistrationCommand;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a dummy user for testing
        user = new User();
        user.setUsername("lecturer1");
        user.setPassword("password");
        user.setFullName("Lecturer One");
        user.setNip("12345678");

        // Initialize the command with mocks
        lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, user);
    }

    @Test
    void testAddUser_InvalidUsername() {
        // Create a user with empty username
        User invalidUser = new User();
        invalidUser.setUsername(""); // Empty username
        invalidUser.setPassword("password");
        invalidUser.setFullName("Lecturer One");
        invalidUser.setNip("12345678");

        // Re-initialize the command with the invalid user
        lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

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
        invalidUser.setUsername("lecturer1");
        invalidUser.setPassword(""); // Empty password
        invalidUser.setFullName("Lecturer One");
        invalidUser.setNip("12345678");

        // Re-initialize the command with the invalid user
        lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

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
        invalidUser.setUsername("lecturer1");
        invalidUser.setPassword("password");
        invalidUser.setFullName(""); // Empty full name
        invalidUser.setNip("12345678");

        // Re-initialize the command with the invalid user
        lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_InvalidNip() {
        // Create a user with empty NIP
        User invalidUser = new User();
        invalidUser.setUsername("lecturer1");
        invalidUser.setPassword("password");
        invalidUser.setFullName("Lecturer One");
        invalidUser.setNip(""); // Empty NIP

        // Re-initialize the command with the invalid user
        lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_UsernameAlreadyExists() {
        // Create a valid user and mock the userRepository behavior
        User validUser = new User();
        validUser.setUsername("lecturer1");
        validUser.setPassword("password");
        validUser.setFullName("Lecturer One");
        validUser.setNip("12345678");

        when(userRepository.existsByUsername(validUser.getUsername())).thenReturn(true);

        // Re-initialize the command with the valid user
        lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, validUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Username already exists", response.get("message"));
    }

    @Test
    void testAddUser_Success() {
        // Mock behavior for userRepository
        when(userRepository.existsByUsername("lecturer1")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Mock the save behavior to return the saved user
        User newUser = new User(UUID.randomUUID(), "lecturer1", "encodedPassword", "Lecturer One", false, "12345678");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("accept", response.get("status"));
        assertEquals("Success register", response.get("messages"));
        assertEquals("lecturer1", response.get("username"));
        assertEquals("LECTURER", response.get("role"));

        // Verify interactions
        verify(userRepository).existsByUsername("lecturer1");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }

    @Test
    void testAddUser_Lecturer_InvalidPayload() {
        // Arrange: Create a user with null or empty properties
        // Testing null or empty username, password, fullName, or nip

        // Create a user with null values for required fields
        User invalidUser = new User(UUID.randomUUID(), null, null, null, true, null);  // All properties are null

        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);  // Simulate that the username doesn't exist

        // Initialize the LecturerRegistrationCommand with the mock user
        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

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
    void testAddUser_Lecturer_ExceptionHandling() {
        // Arrange: Create a user with valid values
        User validUser = new User(UUID.randomUUID(), "lecturer1", "encodedPassword", "Lecturer One", true, "12345678");

        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);  // Simulate that the username doesn't exist
        when(passwordEncoder.encode("encodedPassword")).thenReturn("encodedPassword");

        // Simulate an exception thrown during the save operation
        doThrow(new RuntimeException("Unexpected database error")).when(userRepository).save(any(User.class));

        // Initialize the LecturerRegistrationCommand with the mock user
        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, validUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        // Assertions
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());  // The status should be 401 due to an error
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);  // Ensure the response body is not null
        assertEquals("error", response.get("status"));  // Assert the error status
        assertEquals("Unexpected database error", response.get("messages"));  // Assert the error message

        // Verify interactions
        verify(userRepository).existsByUsername("lecturer1");
        verify(userRepository).save(any(User.class));  // Ensure save() was invoked
        verify(passwordEncoder).encode("encodedPassword");
    }


    @Test
    void testAddUser_Lecturer_EmptyFields() {
        // Arrange: Create a user with empty properties
        User invalidUser = new User(UUID.randomUUID(), "", "", "", true, "");  // All required fields are empty strings

        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);  // Simulate that the username doesn't exist

        // Initialize the LecturerRegistrationCommand with the mock user
        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

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
    void testAddUser_Lecturer_NullPassword() {
        // Arrange: Create a user with null password
        User invalidUser = new User(UUID.randomUUID(), "lecturer1", null, "Lecturer One", true, "12345678");  // Password is null

        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);  // Simulate that the username doesn't exist

        // Initialize the LecturerRegistrationCommand with the mock user
        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

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
    void testAddUser_Lecturer_NullFullname() {
        // Arrange: Create a user with null password
        User invalidUser = new User(UUID.randomUUID(), "lecturer1", "aksj", null, true, "12345678");  // Password is null

        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);  // Simulate that the username doesn't exist

        // Initialize the LecturerRegistrationCommand with the mock user
        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

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
    void testAddUser_Lecturer_NullNIP() {
        // Arrange: Create a user with null password
        User invalidUser = new User(UUID.randomUUID(), "lecturer1", "aaa", "Lecturer One", true, null);  // Password is null

        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);  // Simulate that the username doesn't exist

        // Initialize the LecturerRegistrationCommand with the mock user
        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

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
