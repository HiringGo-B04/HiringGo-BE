package id.ac.ui.cs.advprog.account.dto.update;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserIntoLecturerDTOTest {

    @Test
    void testUserIntoLecturerDTO_ConstructorSetsFieldsCorrectly() {
        // Given
        String fullName = "Dr. John Doe";
        String nip = "1978123456789012";
        String username = "johndoe";
        String role = "LECTURER";

        // When
        UserIntoLecturerDTO dto = new UserIntoLecturerDTO(fullName, nip, username, role);

        // Then
        assertEquals(fullName, dto.fullName);
        assertEquals(nip, dto.nip);
        assertEquals(username, dto.username);
        assertEquals(role, dto.role);
    }

    @Test
    void testUserIntoLecturerDTO_FieldsAreNotNull() {
        // Given
        UserIntoLecturerDTO dto = new UserIntoLecturerDTO("Dr. John Doe", "1978123456789012", "johndoe", "LECTURER");

        // Then
        assertNotNull(dto.fullName);
        assertNotNull(dto.nip);
        assertNotNull(dto.username);
        assertNotNull(dto.role);
    }
}
