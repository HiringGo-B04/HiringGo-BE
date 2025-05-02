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
        user = new User();
        user.setUsername("lecturer1@gmail.com");
        user.setPassword("password");
        user.setFullName("Lecturer One");
        user.setNip("12345678");

        lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, user);
    }

    @Test
    void testAddUser_InvalidUsername() {
        User invalidUser = new User();
        invalidUser.setUsername("");
        invalidUser.setPassword("password");
        invalidUser.setFullName("Lecturer One");
        invalidUser.setNip("12345678");

        lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_NNUllUsername() {
        User invalidUser = new User();
        invalidUser.setUsername(null);
        invalidUser.setPassword("password");
        invalidUser.setFullName("Lecturer One");
        invalidUser.setNip("12345678");

        lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_InvalidPassword() {
        User invalidUser = new User();
        invalidUser.setUsername("lecturer1@gmail.com");
        invalidUser.setPassword(""); // Empty password
        invalidUser.setFullName("Lecturer One");
        invalidUser.setNip("12345678");

        lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_InvalidFullName() {
        User invalidUser = new User();
        invalidUser.setUsername("lecturer1@gmail.com");
        invalidUser.setPassword("password");
        invalidUser.setFullName("");
        invalidUser.setNip("12345678");

        lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_InvalidNip() {
        User invalidUser = new User();
        invalidUser.setUsername("lecturer1@gmail.com");
        invalidUser.setPassword("password");
        invalidUser.setFullName("Lecturer One");
        invalidUser.setNip("");

        lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_UsernameAlreadyExists() {
        User validUser = new User();
        validUser.setUsername("lecturer1@gmail.com");
        validUser.setPassword("password");
        validUser.setFullName("Lecturer One");
        validUser.setNip("12345678");

        when(userRepository.existsByUsername(validUser.getUsername())).thenReturn(true);

        lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, validUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Username already exists", response.get("message"));
    }

    @Test
    void testAddUser_Success() {
        when(userRepository.existsByUsername("lecturer1@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User newUser = new User(UUID.randomUUID(), "lecturer1@gmail.com", "encodedPassword", "Lecturer One", false, "12345678");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("accept", response.get("status"));
        assertEquals("Success register", response.get("messages"));
        assertEquals("lecturer1@gmail.com", response.get("username"));
        assertEquals("LECTURER", response.get("role"));

        verify(userRepository).existsByUsername("lecturer1@gmail.com");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }

    @Test
    void testAddUser_Lecturer_InvalidPayload() {
        User invalidUser = new User(UUID.randomUUID(), null, null, null, true, null);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_Lecturer_ExceptionHandling() {
        User validUser = new User(UUID.randomUUID(), "lecturer1@gmail.com", "encodedPassword", "Lecturer One", true, "12345678");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode("encodedPassword")).thenReturn("encodedPassword");

        doThrow(new RuntimeException("Unexpected database error")).when(userRepository).save(any(User.class));

        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, validUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Unexpected database error", response.get("messages"));

        verify(userRepository).existsByUsername("lecturer1@gmail.com");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("encodedPassword");
    }


    @Test
    void testAddUser_Lecturer_EmptyFields() {
        User invalidUser = new User(UUID.randomUUID(), "", "", "", true, "");  // All required fields are empty strings

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_Lecturer_NullPassword() {
        User invalidUser = new User(UUID.randomUUID(), "lecturer1@gmail.com", null, "Lecturer One", true, "12345678");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_Lecturer_NullFullname() {
        User invalidUser = new User(UUID.randomUUID(), "lecturer1@gmail.com", "aksj", null, true, "12345678");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_Lecturer_NullNIP() {
        User invalidUser = new User(UUID.randomUUID(), "lecturer1@gmail.com", "aaa", "Lecturer One", true, null);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void testAddUser_Lecturer_InvalidUsername() {
        User invalidUser = new User(UUID.randomUUID(), "lecturer1", "aaa", "Lecturer One", true, "12345678" );

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Username must be a valid email address", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_Lecturer_InvalidNIP() {
        User invalidUser = new User(UUID.randomUUID(), "lecturer1@gmail.com", "aaa", "Lecturer One", true, "1234567jdksjd8" );

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("NIM/NIP must only contain number and maximal 12 digits long", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_Lecturer_InvalidName() {
        User invalidUser = new User(UUID.randomUUID(), "lecturer1@gmail.com", "aaa", "Lecturer 123", true, "12345678" );

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Name must only contain letter character", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }


}
