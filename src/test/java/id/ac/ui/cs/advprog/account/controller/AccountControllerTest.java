package id.ac.ui.cs.advprog.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.account.dto.get.GetAllUserDTO;
import id.ac.ui.cs.advprog.account.dto.update.*;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.controller.TestSecurityBeansConfig;
import id.ac.ui.cs.advprog.account.dto.delete.DeleteRequestDTO;
import id.ac.ui.cs.advprog.account.dto.delete.DeleteResponseDTO;
import id.ac.ui.cs.advprog.account.service.AccountService;
import id.ac.ui.cs.advprog.authjwt.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import({SecurityConfig.class, TestSecurityBeansConfig.class})
@TestPropertySource(properties = {
        "jwt.secret=fakeTestSecretKeyThatIsLongEnoughForHmacSha",
        "jwt.expiration=3600000"
})
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService accountService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public AccountService accountService() {
            return Mockito.mock(AccountService.class);
        }
    }

    private static final String DELETE_ENDPOINT = "/api/account/admin/user";

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_shouldReturn200AndUserList() throws Exception {
        User user1 = new User();
        user1.setUserId(UUID.randomUUID());
        user1.setUsername("admin1");
        user1.setRole("ADMIN");

        User user2 = new User();
        user2.setUserId(UUID.randomUUID());
        user2.setUsername("student1");
        user2.setRole("STUDENT");

        GetAllUserDTO responseDto = new GetAllUserDTO("accept", "test", 1, 1, 1, 1, List.of(user1, user2));

        // Mock the CompletableFuture-returning service method
        when(accountService.getAllUser())
                .thenReturn(CompletableFuture.completedFuture(new ResponseEntity<>(responseDto, HttpStatus.OK)));

        mockMvc.perform(MockMvcRequestBuilders.get(DELETE_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("accept"))
                .andExpect(jsonPath("$.message").value("test"))
                .andExpect(jsonPath("$.users.length()").value(2))
                .andExpect(jsonPath("$.users[0].username").value("admin1"))
                .andExpect(jsonPath("$.users[1].username").value("student1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserToLecturer_shouldReturn200() throws Exception {
        UserIntoLecturerDTO request = new UserIntoLecturerDTO("", "", "", "");
        request.username = "lecturer_user";
        request.role = "LECTURER";
        request.fullName = "Dr. Smith";
        request.nip = "1987654321";

        ResponseUpdateDTO response = new ResponseUpdateDTO("accept", "User updated to LECTURER");

        when(accountService.update(any(UserUpdateDTO.class)))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        mockMvc.perform(MockMvcRequestBuilders.patch(DELETE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("accept"))
                .andExpect(jsonPath("$.message").value("User updated to LECTURER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserToStudent_shouldReturn200() throws Exception {
        UserIntoStudentDTO request = new UserIntoStudentDTO("", "", "", "");
        request.username = "student_user";
        request.role = "STUDENT";
        request.fullName = "Jane Student";
        request.nim = "2106700012";

        ResponseUpdateDTO response = new ResponseUpdateDTO("accept", "User updated to STUDENT");

        when(accountService.update(any(UserUpdateDTO.class)))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        mockMvc.perform(MockMvcRequestBuilders.patch(DELETE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("accept"))
                .andExpect(jsonPath("$.message").value("User updated to STUDENT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_shouldReturn200() throws Exception {
        UserUpdateDTO request = new UserIntoAdminDTO("", "");
        request.username = "johndoe";
        request.role = "ADMIN";

        ResponseUpdateDTO response = new ResponseUpdateDTO("accept", "User updated to ADMIN");

        when(accountService.update(any(UserUpdateDTO.class)))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        mockMvc.perform(MockMvcRequestBuilders.patch(DELETE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("accept"))
                .andExpect(jsonPath("$.message").value("User updated to ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_userNotFound_shouldReturn400() throws Exception {
        UserUpdateDTO request = new UserIntoAdminDTO("", "");
        request.username = "unknownuser";
        request.role = "ADMIN";

        ResponseUpdateDTO response = new ResponseUpdateDTO("error", "User not found");

        when(accountService.update(any(UserUpdateDTO.class)))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));

        mockMvc.perform(MockMvcRequestBuilders.patch(DELETE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("User not found"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_shouldReturn200() throws Exception {
        DeleteRequestDTO request = new DeleteRequestDTO("user@example.com");
        DeleteResponseDTO response = new DeleteResponseDTO("success", "Success delete user");

        when(accountService.delete(any(DeleteRequestDTO.class)))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        mockMvc.perform(delete(DELETE_ENDPOINT)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success delete user"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_notFound_shouldReturn400() throws Exception {
        DeleteRequestDTO request = new DeleteRequestDTO("nonexistent@example.com");
        DeleteResponseDTO response = new DeleteResponseDTO("error", "User not found");

        when(accountService.delete(any(DeleteRequestDTO.class)))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));

        mockMvc.perform(delete(DELETE_ENDPOINT)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}
