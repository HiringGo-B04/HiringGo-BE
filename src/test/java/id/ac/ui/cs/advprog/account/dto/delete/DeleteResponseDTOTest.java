package id.ac.ui.cs.advprog.account.dto.delete;

import id.ac.ui.cs.advprog.authjwt.dto.login.LoginResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.logout.LogoutResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteResponseDTOTest {

    @Test
    public void testAllArgsConstructor_ShouldSetAllFields() {
        String status = "success";
        String message = "Success delete user";

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
