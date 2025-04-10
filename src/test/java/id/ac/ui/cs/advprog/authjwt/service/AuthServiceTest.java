package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.authjwt.model.Token;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.TokenRepository;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

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
    public void testRegister_UserAlreadyExists() {
        User user = new User(null, "existinguser", "pass");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        ResponseEntity<Map<String, String>> response = authService.register(user);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Username already exists", response.getBody().get("messages"));
    }

    @Test
    public void testRegister_Success() {
        User input = new User(null, "newuser", "plainpassword");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("plainpassword")).thenReturn("encodedPassword");

        ResponseEntity<Map<String, String>> response = authService.register(input);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Success register", response.getBody().get("messages"));
        assertEquals("newuser", response.getBody().get("username"));
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
    public void testRegister_ThrowsException() {
        User input = new User(null, "newuser", "password");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        doThrow(new RuntimeException("Database is down")).when(userRepository).save(any(User.class));

        ResponseEntity<Map<String, String>> response = authService.register(input);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Database is down", response.getBody().get("messages"));
    }

}
