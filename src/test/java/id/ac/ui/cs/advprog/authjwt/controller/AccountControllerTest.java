package id.ac.ui.cs.advprog.authjwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.eq;
import static org.hamcrest.Matchers.is;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    private ObjectMapper objectMapper;
    private User testUser;
    private Map<String, String> successResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        testUser = new User();
        testUser.setUsername("admin@gmail.com");

        successResponse = new HashMap<>();
        successResponse.put("status", "error");
        successResponse.put("messages", "Succes delete user");
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        Mockito.when(accountService.delete(eq("admin@gmail.com")))
                .thenReturn(new ResponseEntity<>(successResponse, HttpStatus.OK));

        mockMvc.perform(post("/api/account/admin/delete")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.messages", is("Succes delete user")));
    }
}
