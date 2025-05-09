package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.dto.*;
import id.ac.ui.cs.advprog.authjwt.repository.TokenRepository;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.service.command.AdminRegistrationCommand;
import id.ac.ui.cs.advprog.authjwt.service.command.RegistrationCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceTest.class);

    private AuthService authService;
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        tokenRepository = mock(TokenRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        authService = new AuthService();
        authService.userRepository = userRepository;
        authService.tokenRepository = tokenRepository;
        authService.encoder = passwordEncoder;
    }

    // Test successful registration for a Student
    @Test
    void testRegister_StudentRole_Success() {
        // Given
        StudentRegistrationDTO validUser = new StudentRegistrationDTO("student@example.com", "password123", "2106754321", "Jane Doe");
        when(userRepository.existsByUsername("student@example.com")).thenReturn(false);
        when(userRepository.existsByNim("2106754321")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        RegisterResponseDTO responseDTO = new RegisterResponseDTO("accept", "Success register", "student@example.com", "STUDENT");
        ResponseEntity<RegisterResponseDTO> expectedResponse = new ResponseEntity<>(responseDTO, HttpStatus.OK);

        // When
        ResponseEntity<RegisterResponseDTO> actualResponse = authService.register(validUser, "student");

        // Then
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals("Success register", actualResponse.getBody().messages());
    }

    // Test failure when Student username already exists
    @Test
    void testRegister_StudentRole_Fail_UsernameExists() {
        // Given
        StudentRegistrationDTO validUser = new StudentRegistrationDTO("student@example.com", "password123", "2106754321", "Jane Doe");
        when(userRepository.existsByUsername("student@example.com")).thenReturn(true);
        when(userRepository.existsByNim("2106754321")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        RegisterResponseDTO responseDTO = new RegisterResponseDTO("error", "Username already exists");
        ResponseEntity<RegisterResponseDTO> expectedResponse = new ResponseEntity<>(responseDTO, HttpStatus.FORBIDDEN);

        // When
        ResponseEntity<RegisterResponseDTO> actualResponse = authService.register(validUser, "student");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertEquals("Username already exists", actualResponse.getBody().messages());
    }

    // Test invalid role for Student registration
    @Test
    public void testRegister_StudentRole_InvalidRole_ShouldReturnErrorResponse() {
        // Given
        StudentRegistrationDTO dto = new StudentRegistrationDTO("student@example.com", "password123", "2106754321", "Jane Doe");

        // When
        ResponseEntity<RegisterResponseDTO> response = authService.register(dto, "invalidRole");

        // Then
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("error", response.getBody().status());
        assertEquals("Role is invalid", response.getBody().messages());
    }

    // Test successful registration for a Lecturer
    @Test
    void testRegister_LecturerRole_Success() {
        // Given
        LecturerRegistrationDTO validUser = new LecturerRegistrationDTO("lecturer@example.com", "password123", "2106754321", "Lecturer");
        when(userRepository.existsByUsername("lecturer@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        RegisterResponseDTO responseDTO = new RegisterResponseDTO("accept", "Success register", "lecturer@example.com", "LECTURER");
        ResponseEntity<RegisterResponseDTO> expectedResponse = new ResponseEntity<>(responseDTO, HttpStatus.OK);

        // When
        ResponseEntity<RegisterResponseDTO> actualResponse = authService.register(validUser, "lecturer");

        // Then
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals("Success register", actualResponse.getBody().messages());
    }

    // Test failure when Lecturer username already exists
    @Test
    void testRegister_LecturerRole_Fail_UsernameExists() {
        // Given
        LecturerRegistrationDTO validUser = new LecturerRegistrationDTO("lecturer@example.com", "password123", "2106754321", "Lecturer");
        when(userRepository.existsByUsername("lecturer@example.com")).thenReturn(true);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        RegisterResponseDTO responseDTO = new RegisterResponseDTO("error", "Username already exists");
        ResponseEntity<RegisterResponseDTO> expectedResponse = new ResponseEntity<>(responseDTO, HttpStatus.FORBIDDEN);

        // When
        ResponseEntity<RegisterResponseDTO> actualResponse = authService.register(validUser, "lecturer");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertEquals("Username already exists", actualResponse.getBody().messages());
    }

    // Test successful registration for an Admin
    @Test
    void testRegister_AdminRole_Success() {
        // Given
        AdminRegistrationDTO validUser = new AdminRegistrationDTO("admin@example.com", "adminPassword123");
        when(userRepository.existsByUsername("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("adminPassword123")).thenReturn("encodedPassword");

        RegisterResponseDTO responseDTO = new RegisterResponseDTO("accept", "Success register", "admin@example.com", "ADMIN");
        ResponseEntity<RegisterResponseDTO> expectedResponse = new ResponseEntity<>(responseDTO, HttpStatus.OK);

        // When
        ResponseEntity<RegisterResponseDTO> actualResponse = authService.register(validUser, "admin");

        // Then
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals("Success register", actualResponse.getBody().messages());
    }

    // Test failure when Admin username already exists
    @Test
    void testRegister_AdminRole_Fail_UsernameExists() {
        // Given
        AdminRegistrationDTO validUser = new AdminRegistrationDTO("admin@example.com", "adminPassword123");
        when(userRepository.existsByUsername("admin@example.com")).thenReturn(true);
        when(passwordEncoder.encode("adminPassword123")).thenReturn("encodedPassword");

        RegisterResponseDTO responseDTO = new RegisterResponseDTO("error", "Username already exists");
        ResponseEntity<RegisterResponseDTO> expectedResponse = new ResponseEntity<>(responseDTO, HttpStatus.FORBIDDEN);

        // When
        ResponseEntity<RegisterResponseDTO> actualResponse = authService.register(validUser, "admin");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertEquals("Username already exists", actualResponse.getBody().messages());
    }

    // Test invalid role for Admin registration
    @Test
    public void testRegister_AdminRole_InvalidRole_ShouldReturnErrorResponse() {
        // Given
        AdminRegistrationDTO dto = new AdminRegistrationDTO("admin@example.com", "adminPassword123");

        // When
        ResponseEntity<RegisterResponseDTO> response = authService.register(dto, "invalidRole");

        // Then
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("error", response.getBody().status());
        assertEquals("Role is invalid", response.getBody().messages());
    }

    // Test when the role is null or empty
    @Test
    public void testRegister_NullRole_ShouldReturnErrorResponse() {
        // Given
        AdminRegistrationDTO dto = new AdminRegistrationDTO("admin@example.com", "adminPassword123");

        // When
        ResponseEntity<RegisterResponseDTO> response = authService.register(dto, null);

        // Then
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("error", response.getBody().status());
        assertEquals("Role is empty", response.getBody().messages());
    }

    @Test
    public void testRegister_EmptyRole_ShouldReturnErrorResponse() {
        // Given
        AdminRegistrationDTO dto = new AdminRegistrationDTO("admin@example.com", "adminPassword123");

        // When
        ResponseEntity<RegisterResponseDTO> response = authService.register(dto, "");

        // Then
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("error", response.getBody().status());
        assertEquals("Role is empty", response.getBody().messages());
    }
}
