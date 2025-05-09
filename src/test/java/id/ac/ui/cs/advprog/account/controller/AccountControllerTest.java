//package id.ac.ui.cs.advprog.account.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import id.ac.ui.cs.advprog.account.service.AccountService;
//import id.ac.ui.cs.advprog.authjwt.model.User;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Map;
//
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(AccountController.class)
//public class AccountControllerTest {
//
//    private final String DELETE_ENDPOINT = "/api/account/admin/delete";
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private AccountService accountService;
//
//    @TestConfiguration
//    static class TestConfig {
//        @Bean
//        public AccountService accountService() {
//            return Mockito.mock(AccountService.class);
//        }
//    }
//
//    @Test
//    void delete_validUser_shouldReturnSuccess() throws Exception {
//        var user = new User();
//        user.setUsername("admin@example.com");
//
//        var responseMap = Map.of("status", "accept", "message", "User deleted successfully");
//        var responseEntity = new ResponseEntity<>(responseMap, HttpStatus.OK);
//
//        Mockito.when(accountService.delete(anyString())).thenReturn(responseEntity);
//
//        mockMvc.perform(post(DELETE_ENDPOINT)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("accept"))
//                .andExpect(jsonPath("$.message").value("User deleted successfully"));
//    }
//
//    @Test
//    void delete_nonexistentUser_shouldReturnError() throws Exception {
//        var user = new User();
//        user.setUsername("notfound@example.com");
//
//        var responseMap = Map.of("status", "error", "message", "User not found");
//        var responseEntity = new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
//
//        Mockito.when(accountService.delete(anyString())).thenReturn(responseEntity);
//
//        mockMvc.perform(post(DELETE_ENDPOINT)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value("error"))
//                .andExpect(jsonPath("$.message").value("User not found"));
//    }
//}
