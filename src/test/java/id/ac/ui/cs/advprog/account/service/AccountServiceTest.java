package id.ac.ui.cs.advprog.account.service;

import id.ac.ui.cs.advprog.account.dto.delete.DeleteRequestDTO;
import id.ac.ui.cs.advprog.account.dto.delete.DeleteResponseDTO;
import id.ac.ui.cs.advprog.account.dto.get.GetAllUserDTO;
import id.ac.ui.cs.advprog.account.dto.update.*;
import id.ac.ui.cs.advprog.account.service.AccountService;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    private AccountService accountService;
    private UserRepository userRepository;
    private LowonganRepository lowonganRepository;
    private MataKuliahRepository mataKuliahRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        lowonganRepository = mock(LowonganRepository.class);
        mataKuliahRepository = mock(MataKuliahRepository.class);
        accountService = new AccountService(userRepository, lowonganRepository, mataKuliahRepository);
    }

    @Test
    void testGetAllUser_Success() {
        // Given
        User student1 = new User(); student1.setRole("STUDENT"); student1.setPassword("pass1");
        User lecturer1 = new User(); lecturer1.setRole("LECTURER"); lecturer1.setPassword("pass2");
        User admin1 = new User(); admin1.setRole("ADMIN"); admin1.setPassword("pass3");

        when(userRepository.findAllByRole("STUDENT")).thenReturn(List.of(student1));
        when(userRepository.findAllByRole("LECTURER")).thenReturn(List.of(lecturer1));
        when(userRepository.findAllByRole("ADMIN")).thenReturn(List.of(admin1));
        MataKuliah mata1 = new MataKuliah();
        MataKuliah mata2 = new MataKuliah();
        when(mataKuliahRepository.findAll()).thenReturn(List.of(mata1, mata2));

        Lowongan low1 = new Lowongan();
        Lowongan low2 = new Lowongan();
        Lowongan low3 = new Lowongan();
        when(lowonganRepository.findAll()).thenReturn(List.of(low1, low2, low3));

        // When
        ResponseEntity<GetAllUserDTO> response = accountService.getAllUser();

        // Then
        GetAllUserDTO dto = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(dto);
        assertEquals("accept", dto.status());
        assertEquals("test", dto.message());
        assertEquals(1, dto.numberOfStudents());
        assertEquals(1, dto.numberOfLectures());
        assertEquals(2, dto.numberOfCourses());
        assertEquals(3, dto.numberOfVacancies());
        assertEquals(3, dto.users().size());

        // All passwords should be null
        dto.users().forEach(user -> assertNull(user.getPassword()));
    }

    @Test
    void testGetAllUser_ExceptionHandling() {
        // Given
        when(userRepository.findAllByRole("STUDENT")).thenThrow(new RuntimeException("DB error"));

        // When
        ResponseEntity<GetAllUserDTO> response = accountService.getAllUser();

        // Then
        GetAllUserDTO dto = response.getBody();
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(dto);
        assertEquals("error", dto.status());
        assertEquals("DB error", dto.message());
        assertEquals(0, dto.numberOfStudents());
        assertEquals(0, dto.numberOfLectures());
        assertEquals(0, dto.numberOfCourses());
        assertEquals(0, dto.numberOfVacancies());
        assertNull(dto.users());
    }

    @Test
    void testUpdateUserRoleToAdmin_Success() {
        // Given
        String username = "adminuser";
        UserIntoAdminDTO dto = new UserIntoAdminDTO();
        dto.username = username;
        dto.role = "ADMIN";

        User user = new User();
        user.setUsername(username);
        user.setRole("STUDENT");

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        // When
        ResponseEntity<ResponseUpdateDTO> response = accountService.update(dto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("accept", response.getBody().status());
        assertEquals("User updated to ADMIN", response.getBody().message());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserRoleToLecturer_Success() {
        // Given
        String username = "adminuser";
        UserIntoLecturerDTO dto = new UserIntoLecturerDTO();
        dto.username = username;
        dto.role = "LECTURER";
        dto.nip = "123412341234";
        dto.fullName = "adminuser";

        User user = new User();
        user.setUsername(username);
        user.setRole("LECTURER");

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        // When
        ResponseEntity<ResponseUpdateDTO> response = accountService.update(dto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("accept", response.getBody().status());
        assertEquals("User updated to LECTURER", response.getBody().message());
    }

    @Test
    void testUpdateUserRoleToStudent_Success() {
        // Given
        String username = "adminuser";
        UserIntoStudentDTO dto = new UserIntoStudentDTO();
        dto.username = username;
        dto.role = "STUDENT";
        dto.nim = "123412341234";
        dto.fullName = "adminuser";

        User user = new User();
        user.setUsername(username);
        user.setRole("STUDENT");

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        // When
        ResponseEntity<ResponseUpdateDTO> response = accountService.update(dto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("accept", response.getBody().status());
        assertEquals("User updated to STUDENT", response.getBody().message());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Given
        UserUpdateDTO dto = new UserIntoAdminDTO();
        dto.username = "ghost";
        dto.role = "ADMIN";

        when(userRepository.findByUsername("ghost")).thenReturn(null);

        // When
        ResponseEntity<ResponseUpdateDTO> response = accountService.update(dto);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().status());
        assertEquals("User not found", response.getBody().message());

        verify(userRepository, times(1)).findByUsername("ghost");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_UnsupportedRole_ShouldReturnError() {
        // Given
        UserUpdateDTO dto = new UserIntoAdminDTO();
        dto.username = "admin";
        dto.role = "MODERATOR"; // unsupported

        User user = new User();
        user.setUsername("admin");
        user.setRole("STUDENT");

        when(userRepository.findByUsername("admin")).thenReturn(user);

        // When
        ResponseEntity<ResponseUpdateDTO> response = accountService.update(dto);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().status());
        assertEquals("Role not found", response.getBody().message());

        verify(userRepository, times(1)).findByUsername("admin");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_ExceptionThrown_ShouldReturnErrorResponse() {
        // Given
        UserUpdateDTO dto = new UserIntoAdminDTO();
        dto.username = "erroruser";
        dto.role = "ADMIN";

        when(userRepository.findByUsername("erroruser"))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When
        ResponseEntity<ResponseUpdateDTO> response = accountService.update(dto);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().status());
        assertEquals("Unexpected error", response.getBody().message());

        verify(userRepository, times(1)).findByUsername("erroruser");
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
        assertEquals("accept", response.getBody().status()); // You might want to change this to "success"
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
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().status());
        assertEquals("Unexpected failure", response.getBody().message());
        verify(userRepository, times(1)).findByUsername(email);
        verify(userRepository, never()).deleteByUsername(anyString());
    }
}
