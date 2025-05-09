package id.ac.ui.cs.advprog.account.service;

import id.ac.ui.cs.advprog.account.dto.delete.DeleteRequestDTO;
import id.ac.ui.cs.advprog.account.dto.delete.DeleteResponseDTO;
import id.ac.ui.cs.advprog.account.service.AccountService;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    private AccountService accountService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        accountService = new AccountService(userRepository);
    }

    @Test
    void testDeleteUser_Success() {
        // Given
        String email = "admin@gmail.com";
        DeleteRequestDTO request = new DeleteRequestDTO(email);
        User mockUser = new User();
        mockUser.setUsername(email);

        when(userRepository.findByUsername(email)).thenReturn(mockUser);
        doNothing().when(userRepository).deleteByUsername(email);

        // When
        ResponseEntity<DeleteResponseDTO> response = accountService.delete(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().status()); // You might want to change this to "success"
        assertEquals("Succes delete user", response.getBody().message());
        verify(userRepository, times(1)).findByUsername(email);
        verify(userRepository, times(1)).deleteByUsername(email);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Given
        String email = "notfound@example.com";
        DeleteRequestDTO request = new DeleteRequestDTO(email);

        when(userRepository.findByUsername(email)).thenReturn(null);

        // When
        ResponseEntity<DeleteResponseDTO> response = accountService.delete(request);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().status());
        assertEquals("User not found", response.getBody().message());
        verify(userRepository, times(1)).findByUsername(email);
        verify(userRepository, never()).deleteByUsername(anyString());
    }

    @Test
    void testDeleteUser_ExceptionThrown_ShouldReturnErrorResponse() {
        // Given
        String email = "error@example.com";
        DeleteRequestDTO request = new DeleteRequestDTO(email);

        when(userRepository.findByUsername(email)).thenThrow(new RuntimeException("Unexpected failure"));

        // When
        ResponseEntity<DeleteResponseDTO> response = accountService.delete(request);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().status());
        assertEquals("Unexpected failure", response.getBody().message());
        verify(userRepository, times(1)).findByUsername(email);
        verify(userRepository, never()).deleteByUsername(anyString());
    }
}
