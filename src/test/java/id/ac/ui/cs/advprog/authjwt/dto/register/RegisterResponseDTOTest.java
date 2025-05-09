package id.ac.ui.cs.advprog.authjwt.dto.register;

import id.ac.ui.cs.advprog.authjwt.dto.registration.RegisterResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterResponseDTOTest {

    @Test
    public void testFullConstructor() {
        String status = "success";
        String messages = "User registered successfully";
        String username = "admin@gmail.com";
        String role = "ADMIN";

        RegisterResponseDTO dto = new RegisterResponseDTO(status, messages, username, role);

        assertEquals(status, dto.status());
        assertEquals(messages, dto.messages());
        assertEquals(username, dto.username());
        assertEquals(role, dto.role());
    }

    @Test
    public void testOverloadedConstructor() {
        String status = "error";
        String messages = "Registration failed";

        RegisterResponseDTO dto = new RegisterResponseDTO(status, messages);

        assertEquals(status, dto.status());
        assertEquals(messages, dto.messages());
        assertNull(dto.username(), "Username should be null in overloaded constructor");
        assertNull(dto.role(), "Role should be null in overloaded constructor");
    }
}
