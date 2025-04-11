package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.authjwt.model.Token;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.TokenRepository;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.authjwt.service.command.AdminRegistrationCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceTest.class);
    private AuthService authService;
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        tokenRepository = mock(TokenRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);

        authService = new AuthService();
        authService.userRepository = userRepository;
        authService.tokenRepository = tokenRepository;
        authService.encoder = passwordEncoder;
        authService.jwtUtils = jwtUtil;
    }

    @Test
    public void testLogin_UserNotFound() {
        User input = new User(null, "testuser", "password");

        when(userRepository.findByUsername("testuser")).thenReturn(null);

        ResponseEntity<Map<String, String>> response = authService.login(input);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody().get("messages"));
    }

    @Test
    public void testLogin_InvalidPassword() {
        User input = new User(null, "testuser", "wrongpassword");
        User existing = new User(UUID.randomUUID(), "testuser", "encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(existing);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        ResponseEntity<Map<String, String>> response = authService.login(input);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Invalid password", response.getBody().get("messages"));
    }

    @Test
    public void testLogin_Success() {
        User input = new User(null, "testuser", "correctpassword");
        User existing = new User(UUID.randomUUID(), "testuser", "encodedPassword");
        String fakeToken = "jwt.token.string";

        when(userRepository.findByUsername("testuser")).thenReturn(existing);
        when(passwordEncoder.matches("correctpassword", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser")).thenReturn(fakeToken);

        ResponseEntity<Map<String, String>> response = authService.login(input);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Success login", response.getBody().get("messages"));
        assertEquals(fakeToken, response.getBody().get("token"));
    }


    @Test
    public void testLogin_ThrowsException() {
        User input = new User(null, "testuser", "password");
        User existing = new User(UUID.randomUUID(), "testuser", "encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(existing);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser")).thenReturn("mocktoken");

        doThrow(new RuntimeException("DB failure")).when(tokenRepository).save(any(Token.class));

        ResponseEntity<Map<String, String>> response = authService.login(input);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("DB failure", response.getBody().get("messages"));
    }

    @Test
    void testLogout_Success() {
        String tokenString = "valid-token";
        Token token = new Token(tokenString);
        when(tokenRepository.findByToken(tokenString)).thenReturn(token);

        doNothing().when(tokenRepository).deleteByToken(tokenString);


        ResponseEntity<Map<String, String>> response = authService.logout(token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Success to logout", response.getBody().get("messages"));
    }

    @Test
    void testLogout_InvalidToken() {
        String tokenString = "invalid-token";
        Token token = new Token(tokenString);

        ResponseEntity<Map<String, String>> response = authService.logout(token);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid Token", response.getBody().get("messages"));
    }

    @Test
    void testRegister_AdminRole_Success() {
        User validUser = new User(UUID.randomUUID(), "adminUser", "password123");
        when(userRepository.existsByUsername("adminUser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        AdminRegistrationCommand adminRegistrationCommand = mock(AdminRegistrationCommand.class);
        when(adminRegistrationCommand.addUser()).thenReturn(new ResponseEntity<>(Map.of("status", "accept", "messages", "Success register"), HttpStatus.OK));

        ResponseEntity<Map<String, String>> response = authService.register(validUser, "admin");  // Use lowercase role

        assertEquals(HttpStatus.OK, response.getStatusCode());
        logger.info("Response: {}", response.getBody().get("messages"));

        assertEquals("accept", response.getBody().get("status"));
        assertEquals("Success register", response.getBody().get("messages"));
    }

    @Test
    void testRegister_LecturerRole_Success() {
        User validUser = new User(UUID.randomUUID(), "lecturerUser", "password123", "Lecturer User", true, "lecturerNIP");
        when(userRepository.existsByUsername("lecturerUser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        ResponseEntity<Map<String, String>> response = authService.register(validUser, "lecturer");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("accept", response.getBody().get("status"));
        assertEquals("Success register", response.getBody().get("messages"));
    }

    @Test
    void testRegister_StudentRole_Success() {
        User validUser = new User(UUID.randomUUID(), "studentUser", "password123", "Student User", false, "studentNIP");
        when(userRepository.existsByUsername("studentUser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        ResponseEntity<Map<String, String>> response = authService.register(validUser, "student");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("accept", response.getBody().get("status"));
        assertEquals("Success register", response.getBody().get("messages"));
    }

    @Test
    void testRegister_InvalidRole() {
        User validUser = new User(UUID.randomUUID(), "userWithInvalidRole", "password123", "User", true, "userNIP");

        ResponseEntity<Map<String, String>> response = authService.register(validUser, "invalidRole");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Role is invalid", response.getBody().get("messages"));
    }

    @Test
    void testRegister_NullRole() {
        User validUser = new User(UUID.randomUUID(), "userWithNullRole", "password123", "User", true, "userNIP");

        ResponseEntity<Map<String, String>> response = authService.register(validUser, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Role is empty", response.getBody().get("messages"));
    }

    @Test
    void testRegister_EmptyRole() {
        User validUser = new User(UUID.randomUUID(), "userWithEmptyRole", "password123", "User", true, "userNIP");

        ResponseEntity<Map<String, String>> response = authService.register(validUser, "");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Role is empty", response.getBody().get("messages"));
    }
}
