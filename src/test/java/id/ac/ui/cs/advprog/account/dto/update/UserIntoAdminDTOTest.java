package id.ac.ui.cs.advprog.account.dto.update;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserIntoAdminDTOTest {

    @Test
    void testUserIntoAdminDTO_ConstructorSetsFieldsCorrectly() {
        // Given
        String role = "ADMIN";
        String username = "adminuser";

        // When
        UserIntoAdminDTO dto = new UserIntoAdminDTO(role, username);

        // Then
        assertEquals(role, dto.role);
        assertEquals(username, dto.username);
    }

    @Test
    void testUserIntoAdminDTO_FieldsAreNotNull() {
        // Given
        UserIntoAdminDTO dto = new UserIntoAdminDTO("ADMIN", "adminuser");

        // Then
        assertNotNull(dto.role);
        assertNotNull(dto.username);
    }
}
