package id.ac.ui.cs.advprog.authjwt.controller;

import id.ac.ui.cs.advprog.authjwt.facade.AuthenticationFacade;
import id.ac.ui.cs.advprog.authjwt.model.Token;
import id.ac.ui.cs.advprog.authjwt.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationFacade authFacade;

    @Test
    void testLogin_Success() {
        User user = new User(null, "testuser@example.com", "password");

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "accept");
        responseBody.put("messages", "Success login");
        responseBody.put("token", "mock-jwt");

        ResponseEntity<Map<String, String>> mockResponse = ResponseEntity.ok(responseBody);

        when(authFacade.login(user)).thenReturn(mockResponse);

        ResponseEntity<Map<String, String>> response = authController.login(user);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Success login", response.getBody().get("messages"));
        assertEquals("mock-jwt", response.getBody().get("token"));

        verify(authFacade, times(1)).login(user);
    }


    @Test
    void testRegisterAdmin_Success() {
        User admin = new User(UUID.randomUUID(), "admin@example.com", "adminpass");

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "success");
        responseBody.put("messages", "Admin registered");

        ResponseEntity<Map<String, String>> mockResponse = ResponseEntity.status(201).body(responseBody);

        when(authFacade.register(admin, "ADMIN")).thenReturn(mockResponse);

        ResponseEntity<Map<String, String>> response = authController.registerAdmin(admin);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Admin registered", response.getBody().get("messages"));

        verify(authFacade, times(1)).register(admin, "ADMIN");
    }

    @Test
    void testRegisterStudent_Success() {
        User student = new User(UUID.randomUUID(), "student@example.com", "studpass", "Student Name", false, "2106751234");

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "success");
        responseBody.put("messages", "Student registered");

        ResponseEntity<Map<String, String>> mockResponse = ResponseEntity.status(201).body(responseBody);

        when(authFacade.register(student, "STUDENT")).thenReturn(mockResponse);

        ResponseEntity<Map<String, String>> response = authController.registerStudent(student);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Student registered", response.getBody().get("messages"));

        verify(authFacade, times(1)).register(student, "STUDENT");
    }

    @Test
    void testRegisterLecturer_Success() {
        User lecturer = new User(UUID.randomUUID(), "lecturer@example.com", "lectpass", "Lecturer Name", true, "NIP123456");

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "success");
        responseBody.put("messages", "Lecturer registered");

        ResponseEntity<Map<String, String>> mockResponse = ResponseEntity.status(201).body(responseBody);

        when(authFacade.register(lecturer, "LECTURER")).thenReturn(mockResponse);

        ResponseEntity<Map<String, String>> response = authController.registerLecturer(lecturer);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Lecturer registered", response.getBody().get("messages"));

        verify(authFacade, times(1)).register(lecturer, "LECTURER");
    }

    @Test
    void testLogout_Success() {
        Token token = new Token("mock-token");

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "success");
        responseBody.put("messages", "Logout successful");

        ResponseEntity<Map<String, String>> mockResponse = ResponseEntity.ok(responseBody);

        when(authFacade.logout(token)).thenReturn(mockResponse);

        ResponseEntity<Map<String, String>> response = authController.logout(token);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logout successful", response.getBody().get("messages"));

        verify(authFacade, times(1)).logout(token);
    }
}
