package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.authjwt.service.command.LecturerRegistrationCommand;
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

        user = new User();
        user.setUsername("student1@gmail.com");
        user.setPassword("password");
        user.setFullName("Student One");
        user.setNim("12345678");

        studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, user);
    }

    @Test
    void testAddUser_Success() {
        when(userRepository.existsByUsername("student1@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User newUser = new User(UUID.randomUUID(), "student1@gmail.com", "encodedPassword", "Student One", false, "12345678");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("accept", response.get("status"));
        assertEquals("Success register", response.get("messages"));
        assertEquals("student1@gmail.com", response.get("username"));
        assertEquals("STUDENT", response.get("role"));

        verify(userRepository).existsByUsername("student1@gmail.com");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }

    @Test
    void testAddUser_InvalidPayload() {
        User invalidUser = new User();
        invalidUser.setUsername("");
        invalidUser.setPassword("");
        invalidUser.setFullName("");
        invalidUser.setNim("");

        studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_UsernameAlreadyExists() {
        when(userRepository.existsByUsername("student1@gmail.com")).thenReturn(true);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Username already exists", response.get("message"));
    }

    @Test
    void testAddUser_NIMAlreadyExists() {
        when(userRepository.existsByNim("12345")).thenReturn(true);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Nim already exists", response.get("message"));
    }

    @Test
    void testAddUser_Exception() {
        when(userRepository.existsByUsername("student1@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Database error", response.get("messages"));
    }

    @Test
    void testAddUser_InvalidUsername() {
        User invalidUser = new User();
        invalidUser.setUsername("");
        invalidUser.setPassword("password");
        invalidUser.setFullName("Student One");
        invalidUser.setNim("12345678");

        studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_NullUsername() {
        User invalidUser = new User();
        invalidUser.setUsername(null);
        invalidUser.setPassword("password");
        invalidUser.setFullName("Student One");
        invalidUser.setNim("12345678");

        studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_InvalidPassword() {
        User invalidUser = new User();
        invalidUser.setUsername("student1@gmail.com");
        invalidUser.setPassword("");
        invalidUser.setFullName("Student One");
        invalidUser.setNim("12345678");

        studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_InvalidFullName() {
        User invalidUser = new User();
        invalidUser.setUsername("student1@gmail.com");
        invalidUser.setPassword("password");
        invalidUser.setFullName("");
        invalidUser.setNim("12345678");

        studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_InvalidNim() {
        User invalidUser = new User();
        invalidUser.setUsername("student1@gmail.com");
        invalidUser.setPassword("password");
        invalidUser.setFullName("Student One");
        invalidUser.setNim(""); // Empty NIM

        studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));
    }

    @Test
    void testAddUser_Student_EmptyFields() {
        User invalidUser = new User(UUID.randomUUID(), "", "", "", false, "");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        StudentRegistrationCommand studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_Student_NullPassword() {
        User invalidUser = new User(UUID.randomUUID(), "student1@gmail.com", null, "Student One", false, "12345678");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        StudentRegistrationCommand studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_Student_NullFullname() {
        User invalidUser = new User(UUID.randomUUID(), "student1@gmail.com", "aksj", null, false, "12345678");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        StudentRegistrationCommand studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_Student_NullNIM() {
        User invalidUser = new User(UUID.randomUUID(), "student1@gmail.com", "aaa", "Student One", false, null);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        StudentRegistrationCommand studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Invalid payload", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_Student_InvalidUsername() {
        User invalidUser = new User();
        invalidUser.setUsername("student1");
        invalidUser.setPassword("password");
        invalidUser.setFullName("Student One");
        invalidUser.setNim("12345678");

        studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Username must be a valid email address", response.get("message"));
    }

    @Test
    void testAddUser_Student_InvalidNIM() {
        User invalidUser = new User(UUID.randomUUID(), "student1@gmail.com", "aaa", "Student One", false, "12abcd3455677");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        StudentRegistrationCommand studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("NIM/NIP must only contain number and maximal 12 digits long", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void testAddUser_Student_InvalidName() {
        User invalidUser = new User(UUID.randomUUID(), "student1@gmail.com", "aaa", "Student 1234", false, "123455677");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        StudentRegistrationCommand studentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<Map<String, String>> responseEntity = studentRegistrationCommand.addUser();

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("Name must only contain letter character", response.get("message"));

        verify(userRepository, never()).save(any(User.class));
    }

}
