package id.ac.ui.cs.advprog.authjwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.authjwt.config.SecurityConfig;
import id.ac.ui.cs.advprog.authjwt.dto.registration.AdminRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.LecturerRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.StudentRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.facade.AuthenticationFacade;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    private String AdminRegistration = "/api/auth/admin/signup";
    private final String LECTURER_REGISTRATION_ENDPOINT = "/api/auth/admin/signup/lecturer";
    private final String STUDENT_REGISTRATION_ENDPOINT =  "/api/auth/public/signup/student";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("authenticationFacade")
    private AuthenticationFacade authFacade;

    @TestConfiguration
    static class MockConfig {
        @Bean
        AuthenticationFacade authenticationFacade() {
            return Mockito.mock(AuthenticationFacade.class);
        }
    }

    @Test
    @WithMockUser // Simulate an authenticated user
    void registerAdmin_shouldReturn200() throws Exception {
        var request = new AdminRegistrationDTO("admin@mail.com", "securepass123");
        var response = new RegisterResponseDTO("accept", "Success register");

        // Mock the register method
        when(authFacade.register(any(AdminRegistrationDTO.class), eq("ADMIN")))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        // Perform the POST request to the endpoint
        mockMvc.perform(post(AdminRegistration)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("accept"))
                .andExpect(jsonPath("$.messages").value("Success register"));
    }

    @Test
    @WithMockUser // Simulate an authenticated user
    void registerAdmin_invalid_shouldReturn401() throws Exception {
        var request = new AdminRegistrationDTO("invalid", "123");
        var response = new RegisterResponseDTO("error", "Username must be a valid email address");

        // Mock the register method
        when(authFacade.register(any(AdminRegistrationDTO.class), eq("ADMIN")))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED));

        // Perform the POST request to the endpoint
        mockMvc.perform(post(AdminRegistration)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.messages").value("Username must be a valid email address"));
    }

    @Test
    void registerAdmin_invalid_shouldReturn400() throws Exception {
        var request = new AdminRegistrationDTO("admin@mail.com", "123");

        // Mock the register method
        when(authFacade.register(any(AdminRegistrationDTO.class), eq("ADMIN")))
                .thenReturn(new ResponseEntity<>(new RegisterResponseDTO("error", "Password is too weak"), HttpStatus.BAD_REQUEST));

        // Perform the POST request to the endpoint
        mockMvc.perform(post(AdminRegistration)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.messages").value("Password is too weak"));
    }


    @Test
    @WithMockUser
    void registerLecturer_shouldReturn200() throws Exception {
        var request = new LecturerRegistrationDTO("lecturer@mail.com", "securepass123", "12345678", "John Doe");
        var response = new RegisterResponseDTO("accept", "Success register");

        when(authFacade.register(any(LecturerRegistrationDTO.class), eq("LECTURER")))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        mockMvc.perform(post(LECTURER_REGISTRATION_ENDPOINT)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("accept"))
                .andExpect(jsonPath("$.messages").value("Success register"));
    }

    @Test
    @WithMockUser
    void registerLecturer_invalidEmail_shouldReturn401() throws Exception {
        var request = new LecturerRegistrationDTO("invalid-email", "securepass123", "12345678", "John Doe");
        var response = new RegisterResponseDTO("error", "Username must be a valid email address");

        when(authFacade.register(any(LecturerRegistrationDTO.class), eq("LECTURER")))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post(LECTURER_REGISTRATION_ENDPOINT)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.messages").value("Username must be a valid email address"));
    }

    @Test
    @WithMockUser
    void registerLecturer_weakPassword_shouldReturn400() throws Exception {
        var request = new LecturerRegistrationDTO("lecturer@mail.com", "123", "12345678", "John Doe");
        var response = new RegisterResponseDTO("error", "Password is too weak");

        when(authFacade.register(any(LecturerRegistrationDTO.class), eq("LECTURER")))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));

        mockMvc.perform(post(LECTURER_REGISTRATION_ENDPOINT)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.messages").value("Password is too weak"));
    }

    @Test
    @WithMockUser
    void registerStudent_shouldReturn200() throws Exception {
        var request = new StudentRegistrationDTO("student@mail.com", "strongpassword", "2106701234", "Jane Doe");
        var response = new RegisterResponseDTO("accept", "Success register");

        when(authFacade.register(any(StudentRegistrationDTO.class), eq("STUDENT")))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        mockMvc.perform(post(STUDENT_REGISTRATION_ENDPOINT)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("accept"))
                .andExpect(jsonPath("$.messages").value("Success register"));
    }

    @Test
    @WithMockUser
    void registerStudent_invalidEmail_shouldReturn401() throws Exception {
        var request = new StudentRegistrationDTO("invalid-email", "strongpassword", "2106701234", "Jane Doe");
        var response = new RegisterResponseDTO("error", "Username must be a valid email address");

        when(authFacade.register(any(StudentRegistrationDTO.class), eq("STUDENT")))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post(STUDENT_REGISTRATION_ENDPOINT)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.messages").value("Username must be a valid email address"));
    }

    @Test
    @WithMockUser
    void registerStudent_weakPassword_shouldReturn400() throws Exception {
        var request = new StudentRegistrationDTO("student@mail.com", "123", "2106701234", "Jane Doe");
        var response = new RegisterResponseDTO("error", "Password is too weak");

        when(authFacade.register(any(StudentRegistrationDTO.class), eq("STUDENT")))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));

        mockMvc.perform(post(STUDENT_REGISTRATION_ENDPOINT)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.messages").value("Password is too weak"));
    }
}
