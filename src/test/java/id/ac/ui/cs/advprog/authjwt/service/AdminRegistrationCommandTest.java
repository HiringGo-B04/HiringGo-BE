package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.dto.AdminRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.authjwt.service.command.AdminRegistrationCommand;
import id.ac.ui.cs.advprog.authjwt.service.command.RegistrationCommand;
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
        AdminRegistrationDTO user = new AdminRegistrationDTO("admin@gmail.com", "password");

        when(userRepository.existsByUsername("admin@gmail.com")).thenReturn(false);  // Simulate the username doesn't exist
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");  // Simulate encoding the password

        AdminRegistrationCommand adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, user);
        ResponseEntity<RegisterResponseDTO> responseEntity = adminRegistrationCommand.addUser();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        RegisterResponseDTO response = responseEntity.getBody();
        assertNotNull(response);

        assertEquals("accept", response.status());
        assertEquals("Success register", response.messages());
        assertEquals("admin@gmail.com", response.username());
        assertEquals("ADMIN", response.role());

        verify(userRepository).existsByUsername("admin@gmail.com");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }

    @Test
    void testAddUser_UsernameAlreadyExists() {
        AdminRegistrationDTO user = new AdminRegistrationDTO( "admin@gmail.com", "password");

        when(userRepository.existsByUsername("admin@gmail.com")).thenReturn(true);

        AdminRegistrationCommand adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, user);
        ResponseEntity<RegisterResponseDTO> responseEntity = adminRegistrationCommand.addUser();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        RegisterResponseDTO response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.status());
        assertEquals("Username already exists", response.messages());

        verify(userRepository).existsByUsername("admin@gmail.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_Exception() {
        AdminRegistrationDTO user = new AdminRegistrationDTO( "admin@gmail.com", "password");

        when(userRepository.existsByUsername("admin@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        doThrow(new RuntimeException("Database error")).when(userRepository).save(any(User.class));

        AdminRegistrationCommand adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, user);
        ResponseEntity<RegisterResponseDTO> responseEntity = adminRegistrationCommand.addUser();

        // Assert the status code
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());

        RegisterResponseDTO response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.status());
        assertEquals("Database error", response.messages());

        verify(userRepository).existsByUsername("admin@gmail.com");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }

//    @Test
//    void testAddUser_Admin_NullPassword() {
//        User invalidUser = new User(UUID.randomUUID(), "admin@gmail.com", null);
//        when(userRepository.existsByUsername(anyString())).thenReturn(false);
//
//        AdminRegistrationCommand adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, invalidUser);
//
//        ResponseEntity<RegisterResponseDTO> responseEntity = adminRegistrationCommand.addUser();
//
//        // Assert the status code
////        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
//
//        RegisterResponseDTO response = responseEntity.getBody();
//        assertNotNull(response);
//        assertEquals("error", response.status());
//        assertEquals("Username must be a valid email address", response.messages());
//
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void testAddUser_Admin_EmptyPassword() {
//        User invalidUser = new User(UUID.randomUUID(), "admin@gmail.com", "");
//        when(userRepository.existsByUsername(anyString())).thenReturn(false);
//
//        AdminRegistrationCommand adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, invalidUser);
//
//        ResponseEntity<RegisterResponseDTO> responseEntity = adminRegistrationCommand.addUser();
//
//        // Assert the status code
//        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
//
//        RegisterResponseDTO response = responseEntity.getBody();
//        assertNotNull(response);
//        assertEquals("error", response.status());
//        assertEquals("Invalid payload", response.messages());
//
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void testAddUser_Admin_NullUsername() {
//        User invalidUser = new User(UUID.randomUUID(), null, "adminPassword");
//        when(userRepository.existsByUsername(anyString())).thenReturn(false);
//
//        AdminRegistrationCommand adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, invalidUser);
//
//        ResponseEntity<RegisterResponseDTO> responseEntity = adminRegistrationCommand.addUser();
//
//        // Assert the status code
//        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
//
//        RegisterResponseDTO response = responseEntity.getBody();
//        assertNotNull(response);
//        assertEquals("error", response.status());
//        assertEquals("Username must be a valid email address", response.messages());
//
//        verify(userRepository, never()).save(any(User.class));
//    }

    @Test
    void testAddUser_Admin_InvalidEmailFormat() {
        // Use an invalid email format
        AdminRegistrationDTO invalidUser = new AdminRegistrationDTO("invalid-email", "adminPassword");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        AdminRegistrationCommand adminRegistrationCommand = new AdminRegistrationCommand(userRepository, passwordEncoder, invalidUser);

        ResponseEntity<RegisterResponseDTO> responseEntity = adminRegistrationCommand.addUser();

        // Assert the status code
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        RegisterResponseDTO response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.status());
        assertEquals("Username must be a valid email address", response.messages());

        verify(userRepository, never()).save(any(User.class));
    }
}
