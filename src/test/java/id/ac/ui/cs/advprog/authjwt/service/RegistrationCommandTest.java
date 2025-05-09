package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.dto.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.StudentRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.UserDTO;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.authjwt.service.command.RegistrationCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationCommandTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    // Dummy implementation for testing abstract class
    static class DummyRegistrationCommand extends RegistrationCommand {
        public DummyRegistrationCommand(UserRepository userRepository, PasswordEncoder passwordEncoder, UserDTO user) {
            super(userRepository, passwordEncoder, user);
        }

        @Override
        public ResponseEntity<RegisterResponseDTO> addUser() {
            return ResponseEntity.ok(new RegisterResponseDTO("dummy", "dummy"));
        }
    }

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
    }

    @Test
    void testValidStudentInput() {
        StudentRegistrationDTO student = new StudentRegistrationDTO("student@example.com", "password", "123456", "John Doe");

        when(userRepository.existsByUsername("student@example.com")).thenReturn(false);

        RegistrationCommand command = new DummyRegistrationCommand(userRepository, passwordEncoder, student);

        Map<String, String> result = command.check_invalid_input("student");

        assertEquals("valid", result.get("message"));
    }

    @Test
    void testInvalidEmail() {
        StudentRegistrationDTO student = new StudentRegistrationDTO("invalid-email", "password", "123456", "John Doe");

        RegistrationCommand command = new DummyRegistrationCommand(userRepository, passwordEncoder, student);

        Map<String, String> result = command.check_invalid_input("student");

        assertEquals("Username must be a valid email address", result.get("message"));
    }

    @Test
    void testUsernameAlreadyExists() {
        StudentRegistrationDTO student = new StudentRegistrationDTO("student@example.com", "password", "123456", "John Doe");

        when(userRepository.existsByUsername("student@example.com")).thenReturn(true);

        RegistrationCommand command = new DummyRegistrationCommand(userRepository, passwordEncoder, student);

        Map<String, String> result = command.check_invalid_input("student");

        assertEquals("Username already exists", result.get("message"));
    }

    @Test
    void testInvalidName() {
        StudentRegistrationDTO student = new StudentRegistrationDTO("student@example.com", "password", "123456", "John123");

        when(userRepository.existsByUsername("student@example.com")).thenReturn(false);

        RegistrationCommand command = new DummyRegistrationCommand(userRepository, passwordEncoder, student);

        Map<String, String> result = command.check_invalid_input("student");

        assertEquals("Name must only contain letter character", result.get("message"));
    }

    @Test
    void testInvalidNim() {
        StudentRegistrationDTO student = new StudentRegistrationDTO("student@example.com", "password", "not-a-number", "John Doe");

        when(userRepository.existsByUsername("student@example.com")).thenReturn(false);

        RegistrationCommand command = new DummyRegistrationCommand(userRepository, passwordEncoder, student);

        Map<String, String> result = command.check_invalid_input("student");

        assertEquals("NIM/NIP must only contain number and maximal 12 digits long", result.get("message"));
    }
}
