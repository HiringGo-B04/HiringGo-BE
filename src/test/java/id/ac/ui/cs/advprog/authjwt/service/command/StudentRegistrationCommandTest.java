package id.ac.ui.cs.advprog.authjwt.service.command;

import id.ac.ui.cs.advprog.authjwt.dto.registration.StudentRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentRegistrationCommandTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private StudentRegistrationDTO validStudentDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validStudentDTO = new StudentRegistrationDTO("student@example.com", "password", "1234567890", "student");
    }

    @Test
    void testAddUser_Success() {
        // Simulate valid lecturer data and ensure user does not exist in the repo
        when(userRepository.existsByNim(validStudentDTO.nim())).thenReturn(false);
        when(userRepository.existsByUsername(validStudentDTO.username())).thenReturn(false);
        when(passwordEncoder.encode(validStudentDTO.password())).thenReturn("encodedPassword");

        StudentRegistrationCommand StudentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, validStudentDTO);
        ResponseEntity<RegisterResponseDTO> responseEntity = StudentRegistrationCommand.addUser();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        RegisterResponseDTO response = responseEntity.getBody();
        assertNotNull(response);

        assertEquals("accept", response.status());
        assertEquals("Success register", response.messages());
        assertEquals(validStudentDTO.username(), response.username());
        assertEquals("STUDENT", response.role());

        verify(userRepository).existsByNim(validStudentDTO.nim());
        verify(userRepository).existsByUsername(validStudentDTO.username());
        verify(passwordEncoder).encode(validStudentDTO.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testAddUser_NimAlreadyExists() {
        when(userRepository.existsByNim(validStudentDTO.nim())).thenReturn(true);

        StudentRegistrationCommand StudentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, validStudentDTO);
        ResponseEntity<RegisterResponseDTO> responseEntity = StudentRegistrationCommand.addUser();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        RegisterResponseDTO response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.status());
        assertEquals("NIM Already Exist", response.messages());

        verify(userRepository).existsByNim(validStudentDTO.nim());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_UsernameAlreadyExists() {
        when(userRepository.existsByUsername(validStudentDTO.username())).thenReturn(true);
        when(userRepository.existsByNim(validStudentDTO.nim())).thenReturn(false);

        StudentRegistrationCommand StudentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, validStudentDTO);
        ResponseEntity<RegisterResponseDTO> responseEntity = StudentRegistrationCommand.addUser();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        RegisterResponseDTO response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.status());
        assertEquals("Username already exists", response.messages());

        verify(userRepository).existsByUsername(validStudentDTO.username());
        verify(userRepository, never()).existsByNim(validStudentDTO.nim());
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void testAddUser_ExceptionHandling() {
        // Simulate unexpected error during user registration
        when(userRepository.existsByNim(validStudentDTO.nim())).thenReturn(false);
        when(userRepository.existsByUsername(validStudentDTO.username())).thenReturn(false);
        when(passwordEncoder.encode(validStudentDTO.password())).thenReturn("encodedPassword");
        doThrow(new RuntimeException("Unexpected error")).when(userRepository).save(any(User.class));

        StudentRegistrationCommand StudentRegistrationCommand = new StudentRegistrationCommand(userRepository, passwordEncoder, validStudentDTO);
        ResponseEntity<RegisterResponseDTO> responseEntity = StudentRegistrationCommand.addUser();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        RegisterResponseDTO response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.status());
        assertEquals("Unexpected error", response.messages());
    }
}
