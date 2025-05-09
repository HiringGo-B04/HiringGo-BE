package id.ac.ui.cs.advprog.authjwt.dto.logout;

import id.ac.ui.cs.advprog.authjwt.dto.login.LoginResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LogoutResponseDTOTest {

    @Test
    public void testAllArgsConstructor_ShouldSetAllFields() {
        String status = "success";
        String message = "Success to logout";

        LogoutResponseDTO dto = new LogoutResponseDTO(status, message);

        assertEquals(status, dto.status());
        assertEquals(message, dto.message());
    }

    @Test
    public void testTwoArgsConstructor_ShouldSetTokenToNull() {
        String status = "error";
        String message = "Invalid credentials";

        LoginResponseDTO dto = new LoginResponseDTO(status, message);

        assertEquals(status, dto.status());
    }
}
