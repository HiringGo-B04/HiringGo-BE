package id.ac.ui.cs.advprog.authjwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.authjwt.config.SecurityConfig;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.HashMap;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import(SecurityConfig.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService accountService;

    @Test
    @WithMockUser
    void testDeleteUser_Success() throws Exception {
        User user = new User();
        user.setUsername("admin@gmail.com");

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("messages", "Succes delete user");

        when(accountService.delete("admin@gmail.com"))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        mockMvc.perform(post("/api/account/admin/delete")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testDeleteUser_UserNotFound() throws Exception {
        User user = new User();
        user.setUsername("unknown@gmail.com");

        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("messages", "User not found");

        when(accountService.delete("unknown@gmail.com"))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.FORBIDDEN));

        mockMvc.perform(post("/api/account/admin/delete")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }
}
