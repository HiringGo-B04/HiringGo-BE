package id.ac.ui.cs.advprog.authjwt.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void testGetValue() {
        assertEquals("ADMIN", UserRole.ADMIN.getValue());
        assertEquals("STUDENT", UserRole.STUDENT.getValue());
        assertEquals("LECTURER", UserRole.LECTURER.getValue());
    }

    @Test
    void testEnumNameMatchesValue() {
        for (UserRole role : UserRole.values()) {
            assertEquals(role.name(), role.getValue());
        }
    }

    @Test
    void testEnumValuesIntegrity() {
        UserRole[] roles = UserRole.values();
        assertEquals(3, roles.length);
        assertArrayEquals(new UserRole[] {
                UserRole.ADMIN, UserRole.STUDENT, UserRole.LECTURER
        }, roles);
    }
}
