package id.ac.ui.cs.advprog.account.service.strategy;

import id.ac.ui.cs.advprog.account.dto.update.ResponseUpdateDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserIntoAdminDTO;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.http.HttpStatus;

public class AdminRoleUpdateStrategyTest {

    private UserRepository userRepository;
    private AdminRoleUpdateStrategy strategy;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        strategy = new AdminRoleUpdateStrategy(userRepository);
    }

    @Test
    void testUpdateRole_Success() {
        // Given
        UserIntoAdminDTO dto = new UserIntoAdminDTO();
        dto.username = "student1";
        dto.role = "ADMIN";

        User user = new User();
        user.setUsername("student1");
        user.setFullName("Student One");
        user.setNip("123456");
        user.setNim("987654");

        when(userRepository.save(user)).thenReturn(user);

        // When
        ResponseEntity<ResponseUpdateDTO> response = strategy.updateRole(dto, user);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("accept", response.getBody().status());
        assertEquals("User updated to ADMIN", response.getBody().message());

        assertNull(user.getNip());
        assertNull(user.getNim());
        assertNull(user.getFullName());
        assertEquals("ADMIN", user.getRole());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateRole_SaveFails() {
        // Given
        UserIntoAdminDTO dto = new UserIntoAdminDTO();
        dto.username = "student1";
        dto.role = "ADMIN";

        User user = new User();
        user.setUsername("student1");

        when(userRepository.save(user)).thenThrow(new RuntimeException("DB error"));

        // When
        ResponseEntity<ResponseUpdateDTO> response = strategy.updateRole(dto, user);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().status());
        assertEquals("DB error", response.getBody().message());

        verify(userRepository, times(1)).save(user);
    }
}
