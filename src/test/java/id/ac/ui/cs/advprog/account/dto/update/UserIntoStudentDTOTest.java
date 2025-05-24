package id.ac.ui.cs.advprog.account.dto.update;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserIntoStudentDTOTest {

    @Test
    void testUserIntoStudentDTO_ConstructorSetsFieldsCorrectly() {
        // Given
        String fullName = "Jane Doe";
        String nim = "2106751234";
        String username = "janedoe";
        String role = "STUDENT";

        // When
        UserIntoStudentDTO dto = new UserIntoStudentDTO(fullName, nim, username, role);

        // Then
        assertEquals(fullName, dto.fullName);
        assertEquals(nim, dto.nim);
        assertEquals(username, dto.username);
        assertEquals(role, dto.role);
    }

    @Test
    void testUserIntoStudentDTO_FieldsAreNotNull() {
        // Given
        UserIntoStudentDTO dto = new UserIntoStudentDTO("Jane Doe", "2106751234", "janedoe", "STUDENT");

        // Then
        assertNotNull(dto.fullName);
        assertNotNull(dto.nim);
        assertNotNull(dto.username);
        assertNotNull(dto.role);
    }
}
