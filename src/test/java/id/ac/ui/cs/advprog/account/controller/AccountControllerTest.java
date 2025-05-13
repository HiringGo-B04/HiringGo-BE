package id.ac.ui.cs.advprog.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.authjwt.testconfig.TestSecurityBeansConfig;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

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
