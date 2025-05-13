package id.ac.ui.cs.advprog.account.service.strategy;

import id.ac.ui.cs.advprog.account.dto.update.ResponseUpdateDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserIntoLecturerDTO;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LecturerRoleUpdateStrategyTest {

    private UserRepository userRepository;
    private LecturerRoleUpdateStrategy strategy;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        strategy = new LecturerRoleUpdateStrategy(userRepository);
    }

    @Test
    void testUpdateRole_Success() {
        // Given
        UserIntoLecturerDTO dto = new UserIntoLecturerDTO();
        dto.nip = "12345678";
        dto.fullName = "John Doe";
        dto.username = "johndoe";
        dto.role = "LECTURER";

        User user = new User();
        user.setUsername("johndoe");

        // When
        when(userRepository.save(user)).thenReturn(user);
        ResponseEntity<ResponseUpdateDTO> response = strategy.updateRole(dto, user);

        // Then
        assertEquals("LECTURER", user.getRole());
        assertEquals("12345678", user.getNip());
        assertEquals("John Doe", user.getFullName()); // Taken from username per current logic
        assertNull(user.getNim());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("accept", response.getBody().status());
        assertEquals("User updated to LECTURER", response.getBody().message());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateRole_RepositoryThrowsException() {
        // Given
        UserIntoLecturerDTO dto = new UserIntoLecturerDTO();
        dto.nip = "999999";
        dto.fullName = "Jane Smith";
        dto.username = "janesmith";
        dto.role = "LECTURER";

        User user = new User();
        user.setUsername("janesmith");

        when(userRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<ResponseUpdateDTO> response = strategy.updateRole(dto, user);

        // Then
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("error", response.getBody().status());
        assertEquals("Database error", response.getBody().message());

        verify(userRepository, times(1)).save(user);
    }
}
