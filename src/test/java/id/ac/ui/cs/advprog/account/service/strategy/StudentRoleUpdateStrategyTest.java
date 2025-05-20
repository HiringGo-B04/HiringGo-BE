package id.ac.ui.cs.advprog.account.service.strategy;

import id.ac.ui.cs.advprog.account.dto.update.ResponseUpdateDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserIntoStudentDTO;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentRoleUpdateStrategyTest {

    private UserRepository userRepository;
    private StudentRoleUpdateStrategy strategy;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        strategy = new StudentRoleUpdateStrategy(userRepository);
    }

    @Test
    void testUpdateRole_Success() {
        // Given
        UserIntoStudentDTO dto = new UserIntoStudentDTO();
        dto.nim = "12345678";
        dto.fullName = "John Doe";
        dto.username = "johndoe";
        dto.role = "STUDENT";

        User user = new User();
        user.setUsername("johndoe");

        // When
        when(userRepository.save(user)).thenReturn(user);
        ResponseEntity<ResponseUpdateDTO> response = strategy.updateRole(dto, user);

        // Then
        assertEquals("STUDENT", user.getRole());
        assertEquals("12345678", user.getNim());
        assertEquals("John Doe", user.getFullName()); // Taken from username per current logic
        assertNull(user.getNip());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("accept", response.getBody().status());
        assertEquals("User updated to STUDENT", response.getBody().message());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateRole_NimAlreadyExists() {
        // Given
        UserIntoStudentDTO dto = new UserIntoStudentDTO();
        dto.nim = "2106754321";
        dto.fullName = "Alice Smith";
        dto.username = "alicesmith";
        dto.role = "STUDENT";

        User user = new User();
        user.setUsername("alicesmith");

        // Mock that the NIM already exists
        when(userRepository.existsByNip(dto.nim)).thenReturn(true);

        // Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            // When
            strategy.updateRole(dto, user);
        });

        assertEquals("NIM with this student already exists", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }


    @Test
    void testUpdateRole_RepositoryThrowsException() {
        // Given
        UserIntoStudentDTO dto = new UserIntoStudentDTO();
        dto.nim = "999999";
        dto.fullName = "Jane Smith";
        dto.username = "janesmith";
        dto.role = "STUDENT";

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
