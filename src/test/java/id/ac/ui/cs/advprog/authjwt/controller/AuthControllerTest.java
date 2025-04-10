package id.ac.ui.cs.advprog.authjwt.controller;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.model.Token;
import id.ac.ui.cs.advprog.authjwt.repository.TokenRepository;
import id.ac.ui.cs.advprog.authjwt.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private TokenRepository tokenRepository;

    private MockMvc mockMvc; // MockMvc to simulate HTTP requests

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build(); // Set up MockMvc for testing the controller
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
        Token token = new Token(tokenString); // Create a Token object

        // Prepare expected response body
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "accept");
        responseBody.put("messages", "Success to logout");

        ResponseEntity<Map<String, String>> mockResponse = ResponseEntity.ok(responseBody);

        // Mock the behavior of authService.logout
        when(authService.logout(token)).thenReturn(mockResponse);

        // Act: Call the logout method through the controller
        ResponseEntity<Map<String, String>> response = authController.logout(token);

        // Assert: Verify the status code and response content
        assertEquals(200, response.getStatusCodeValue());  // HTTP status is OK (200)
        assertEquals("Success to logout", response.getBody().get("messages"));
        assertEquals("accept", response.getBody().get("status"));

        // Verify that the logout method in authService was called once with the token
        verify(authService, times(1)).logout(token);
    }
    @Test
    void testLogout_Failure_InvalidToken() {
        // Arrange
        String tokenString = "invalid-token";
        Token token = new Token(tokenString); // Create a Token object with an invalid token

        // Prepare expected response body for failure
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "error");
        responseBody.put("messages", "Invalid Token");

        ResponseEntity<Map<String, String>> mockResponse = ResponseEntity.status(401).body(responseBody);

        // Mock the behavior of authService.logout for an invalid token
        when(authService.logout(token)).thenReturn(mockResponse);

        // Act: Call the logout method through the controller
        ResponseEntity<Map<String, String>> response = authController.logout(token);

        // Assert: Verify the status code and response content for failure
        assertEquals(401, response.getStatusCodeValue());  // HTTP status is Unauthorized (401)
        assertEquals("Invalid Token", response.getBody().get("messages"));
        assertEquals("error", response.getBody().get("status"));

        // Verify that the logout method in authService was called once with the invalid token
        verify(authService, times(1)).logout(token);
    }
}
