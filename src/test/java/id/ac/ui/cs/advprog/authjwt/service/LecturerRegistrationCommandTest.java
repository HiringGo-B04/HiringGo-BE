package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.dto.registration.LecturerRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.authjwt.service.command.LecturerRegistrationCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LecturerRegistrationCommandTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private LecturerRegistrationDTO validLecturerDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validLecturerDTO = new LecturerRegistrationDTO("lecturer@example.com", "password", "1234567890", "lecturer");
    }

    @Test
    void testAddUser_Success() {
        // Simulate valid lecturer data and ensure user does not exist in the repo
        when(userRepository.existsByNip(validLecturerDTO.nip())).thenReturn(false);
        when(userRepository.existsByUsername(validLecturerDTO.username())).thenReturn(false);
        when(passwordEncoder.encode(validLecturerDTO.password())).thenReturn("encodedPassword");

        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, validLecturerDTO);
        ResponseEntity<RegisterResponseDTO> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        RegisterResponseDTO response = responseEntity.getBody();
        assertNotNull(response);

        assertEquals("accept", response.status());
        assertEquals("Success register", response.messages());
        assertEquals(validLecturerDTO.username(), response.username());
        assertEquals("LECTURER", response.role());

        verify(userRepository).existsByNip(validLecturerDTO.nip());
        verify(userRepository).existsByUsername(validLecturerDTO.username());
        verify(passwordEncoder).encode(validLecturerDTO.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testAddUser_NipAlreadyExists() {
        // Simulate NIP already existing in the repository
        when(userRepository.existsByNip(validLecturerDTO.nip())).thenReturn(true);

        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, validLecturerDTO);
        ResponseEntity<RegisterResponseDTO> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        RegisterResponseDTO response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.status());
        assertEquals("NIP Already exists", response.messages());

        verify(userRepository).existsByNip(validLecturerDTO.nip());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_UsernameAlreadyExists() {
        when(userRepository.existsByUsername(validLecturerDTO.username())).thenReturn(true);
        when(userRepository.existsByNip(validLecturerDTO.nip())).thenReturn(false);

        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, validLecturerDTO);
        ResponseEntity<RegisterResponseDTO> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        RegisterResponseDTO response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.status());
        assertEquals("Username already exists", response.messages());

        verify(userRepository).existsByUsername(validLecturerDTO.username());
        verify(userRepository, never()).existsByNip(validLecturerDTO.nip());
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void testAddUser_ExceptionHandling() {
        // Simulate unexpected error during user registration
        when(userRepository.existsByNip(validLecturerDTO.nip())).thenReturn(false);
        when(userRepository.existsByUsername(validLecturerDTO.username())).thenReturn(false);
        when(passwordEncoder.encode(validLecturerDTO.password())).thenReturn("encodedPassword");
        doThrow(new RuntimeException("Unexpected error")).when(userRepository).save(any(User.class));

        LecturerRegistrationCommand lecturerRegistrationCommand = new LecturerRegistrationCommand(userRepository, passwordEncoder, validLecturerDTO);
        ResponseEntity<RegisterResponseDTO> responseEntity = lecturerRegistrationCommand.addUser();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        RegisterResponseDTO response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("error", response.status());
        assertEquals("Unexpected error", response.messages());
    }
}
