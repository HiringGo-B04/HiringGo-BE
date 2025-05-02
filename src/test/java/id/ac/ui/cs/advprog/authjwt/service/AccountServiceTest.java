package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    private AccountService accountService;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        accountService = new AccountService();
        accountService.userRepository = userRepository;
    }

    @Test
    public void testDeleteUser_Successful() {
        String email = "admin@gmail.com";
        User mockUser = new User();
        mockUser.setUsername(email);

        when(userRepository.findByUsername(email)).thenReturn(mockUser);
        doNothing().when(userRepository).deleteByUsername(email);

        ResponseEntity<Map<String, String>> response = accountService.delete(email);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Succes delete user", response.getBody().get("messages"));
        assertEquals("error", response.getBody().get("status")); // You might want to change this to "success"
    }

    @Test
    public void testDeleteUser_EmailIsNull() {
        ResponseEntity<Map<String, String>> response = accountService.delete(null);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Email is empty", response.getBody().get("messages"));
        assertEquals("error", response.getBody().get("status"));
    }

    @Test
    public void testDeleteUser_EmailIsEmpty() {
        ResponseEntity<Map<String, String>> response = accountService.delete("");

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Email is empty", response.getBody().get("messages"));
        assertEquals("error", response.getBody().get("status"));
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByUsername(email)).thenReturn(null);

        ResponseEntity<Map<String, String>> response = accountService.delete(email);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody().get("messages"));
        assertEquals("error", response.getBody().get("status"));
    }
}
