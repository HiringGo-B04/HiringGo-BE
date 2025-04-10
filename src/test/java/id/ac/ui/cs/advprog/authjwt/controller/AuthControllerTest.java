package id.ac.ui.cs.advprog.authjwt.controller;

import id.ac.ui.cs.advprog.authjwt.model.User;
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
}
