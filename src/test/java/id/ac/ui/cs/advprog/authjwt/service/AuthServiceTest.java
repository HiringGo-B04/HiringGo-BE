package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.authjwt.dto.login.LoginRequestDTO;
import id.ac.ui.cs.advprog.authjwt.dto.login.LoginResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.logout.LogoutRequestDTO;
import id.ac.ui.cs.advprog.authjwt.dto.logout.LogoutResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.AdminRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.LecturerRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.StudentRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.model.Token;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.TokenRepository;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceTest.class);

    private AuthService authService;
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private JwtUtil jwtUtils;
    private PasswordEncoder passwordEncoder;
    private MeterRegistry meterRegistry;

    private Counter mockCounter;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        tokenRepository = mock(TokenRepository.class);
        jwtUtils = mock(JwtUtil.class);
        passwordEncoder = mock(PasswordEncoder.class);
        meterRegistry = mock(MeterRegistry.class);

        authService = new AuthService(meterRegistry);
        authService.userRepository = userRepository;
        authService.tokenRepository = tokenRepository;
        authService.jwtUtils = jwtUtils;
        authService.encoder = passwordEncoder;

        mockCounter = mock(Counter.class);
        when(meterRegistry.counter("login.success.count")).thenReturn(mockCounter);
    }

    @Test
    void testLogout_Success() {
        // Given
        LogoutRequestDTO logoutRequest = new LogoutRequestDTO("validToken");
        Token token = new Token("validToken");
        // No exception should be thrown during delete
        when(tokenRepository.findByToken("validToken")).thenReturn(token);

        // When
        ResponseEntity<LogoutResponseDTO> response = authService.logout(logoutRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("accept", response.getBody().status());
        assertEquals("Succes to logout", response.getBody().message());
        verify(tokenRepository, times(1)).deleteByToken("validToken");
    }

    @Test
    void testLogout_TokenNotFound() {
        // Given
        LogoutRequestDTO logoutRequest = new LogoutRequestDTO("invalidToken");

        when(tokenRepository.findByToken("invalidToken")).thenReturn(null);
        // When
        ResponseEntity<LogoutResponseDTO> response = authService.logout(logoutRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().status());
        assertEquals("Token not found", response.getBody().message());
    }


    @Test
    void testLogin_Success() {
        // Given
        LoginRequestDTO loginRequest = new LoginRequestDTO("user@example.com", "password123");
        User mockUser = new User();
        mockUser.setUsername("user@example.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setRole("STUDENT");
        mockUser.setUserId(UUID.randomUUID());

        when(userRepository.findByUsername("user@example.com")).thenReturn(mockUser);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken(mockUser.getUsername(), "STUDENT", mockUser.getUserId()))
                .thenReturn("mocked-jwt-token");

        // When
        ResponseEntity<LoginResponseDTO> response = authService.login(loginRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("accept", response.getBody().status());
        assertEquals("Success login", response.getBody().message());
        assertEquals("mocked-jwt-token", response.getBody().token());
    }

    @Test
    void testLogin_Failed_WrongPassword() {
        // Given
        LoginRequestDTO loginRequest = new LoginRequestDTO("user@example.com", "wrongPassword");
        User mockUser = new User();
        mockUser.setUsername("user@example.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setRole("STUDENT");

        when(userRepository.findByUsername("user@example.com")).thenReturn(mockUser);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // When
        ResponseEntity<LoginResponseDTO> response = authService.login(loginRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().status());
        assertEquals("Invalid Password", response.getBody().message());
    }

    @Test
    void login_userNotFound_shouldReturnErrorResponse() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("notfound@example.com", "password");
        when(userRepository.findByUsername("notfound@example.com")).thenReturn(null);

        // Act
        ResponseEntity<LoginResponseDTO> response = authService.login(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().status());
        assertEquals("User didn't exist", response.getBody().message());
    }

    @Test
    void login_exceptionDuringTokenGeneration_shouldReturnErrorResponse() {
        // Arrange
        var request = new LoginRequestDTO("user@example.com", "password");
        var user = new User();
        user.setUsername("user@example.com");
        user.setPassword("encodedPass");
        user.setRole("STUDENT");
        user.setUserId(UUID.randomUUID());

        when(userRepository.findByUsername("user@example.com")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPass")).thenReturn(true);
        when(jwtUtils.generateToken("user@example.com", "STUDENT", user.getUserId())).thenThrow(new RuntimeException("JWT failure"));

        // Act
        ResponseEntity<LoginResponseDTO> response = authService.login(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error on prod", response.getBody().status());
        assertEquals("JWT failure", response.getBody().message());
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
