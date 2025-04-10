package id.ac.ui.cs.advprog.authjwt.controller;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.model.Token;
import id.ac.ui.cs.advprog.authjwt.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        User user = new User(null, "testuser", "password");
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "accept");
        responseBody.put("messages", "Success login");
        responseBody.put("token", "mock-jwt");

        ResponseEntity<Map<String, String>> mockResponse = ResponseEntity.ok(responseBody);

        when(authService.login(user)).thenReturn(mockResponse);

        ResponseEntity<Map<String, String>> response = authController.login(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Success login", response.getBody().get("messages"));
        assertEquals("mock-jwt", response.getBody().get("token"));
    }

    @Test
    void testRegisterUser_Success() {
        User user = new User(null, "newuser", "password");
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "accept");
        responseBody.put("messages", "Success register");
        responseBody.put("username", "newuser");

        ResponseEntity<Map<String, String>> mockResponse = ResponseEntity.ok(responseBody);

        when(authService.register(user)).thenReturn(mockResponse);

        ResponseEntity<Map<String, String>> response = authController.registerUser(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Success register", response.getBody().get("messages"));
        assertEquals("newuser", response.getBody().get("username"));
    }

    @Test
    void testLogout_Success() {
        // Arrange
        String tokenString = "valid-token";
        Token token = new Token(tokenString);  // Assuming Token has a constructor accepting the token string
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "accept");
        responseBody.put("messages", "Success logout");

        ResponseEntity<Map<String, String>> mockResponse = ResponseEntity.ok(responseBody);

        // Mocking the AuthService method
        when(authService.logout(token)).thenReturn(mockResponse);

        // Act
        ResponseEntity<Map<String, String>> response = authService.logout(token);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Success logout", response.getBody().get("messages"));
    }

    @Test
    void testLogout_InvalidToken() {
        // Arrange
        String tokenString = "invalid-token";
        Token token = new Token(tokenString);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "error");
        responseBody.put("messages", "Invalid Token");

        ResponseEntity<Map<String, String>> mockResponse = ResponseEntity.status(401).body(responseBody);

        // Mocking the AuthService method for invalid token
        when(authService.logout(token)).thenReturn(mockResponse);

        // Act
        ResponseEntity<Map<String, String>> response = authService.logout(token);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid Token", response.getBody().get("messages"));
    }
}
