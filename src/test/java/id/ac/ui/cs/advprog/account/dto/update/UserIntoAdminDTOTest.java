package id.ac.ui.cs.advprog.account.dto.update;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserIntoAdminDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAllFieldsAreValid_thenNoConstraintViolations() {
        UserIntoAdminDTO dto = new UserIntoAdminDTO();
        dto.role = "ADMIN";
        dto.username = "adminUser";

        Set<ConstraintViolation<UserIntoAdminDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations for valid input");
    }

    @Test
    public void whenUsernameIsBlank_thenValidationFails() {
        UserIntoAdminDTO dto = new UserIntoAdminDTO();
        dto.role = "ADMIN";
        dto.username = "";

        Set<ConstraintViolation<UserIntoAdminDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Expected a violation for blank username");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    public void whenRoleIsBlank_thenValidationFails() {
        UserIntoAdminDTO dto = new UserIntoAdminDTO();
        dto.role = "";
        dto.username = "adminUser";

        Set<ConstraintViolation<UserIntoAdminDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Expected a violation for blank role");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("role")));
    }

    @Test
    public void whenBothFieldsAreBlank_thenTwoViolations() {
        UserIntoAdminDTO dto = new UserIntoAdminDTO();
        dto.role = "";
        dto.username = "";

        Set<ConstraintViolation<UserIntoAdminDTO>> violations = validator.validate(dto);
        assertEquals(2, violations.size(), "Expected two violations for blank role and username");
    }
}
