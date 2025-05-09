package id.ac.ui.cs.advprog.authjwt.dto.login;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginResponseDTOTest {

    @Test
    public void testAllArgsConstructor_ShouldSetAllFields() {
        String status = "success";
        String message = "Login successful";
        String token = "jwt.token.here";

        LoginResponseDTO dto = new LoginResponseDTO(status, message, token);

        assertEquals(status, dto.status());
        assertEquals(message, dto.message());
        assertEquals(token, dto.token());
    }

    @Test
    public void testTwoArgsConstructor_ShouldSetTokenToNull() {
        String status = "error";
        String message = "Invalid credentials";

        LoginResponseDTO dto = new LoginResponseDTO(status, message);

        assertEquals(status, dto.status());
        assertEquals(message, dto.message());
        assertNull(dto.token());
    }
}
