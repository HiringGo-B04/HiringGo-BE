package id.ac.ui.cs.advprog.account.dto.update;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserIntoAdminDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAllFieldsValid_thenNoViolations() {
        UserIntoAdminDTO dto = new UserIntoAdminDTO(
                "user@example.com"
        );

        Set<ConstraintViolation<UserIntoAdminDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    public void whenFieldsBlank_thenViolations() {
        UserIntoAdminDTO dto = new UserIntoAdminDTO(
                ""
        );

        Set<ConstraintViolation<UserIntoAdminDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Expected 1 constraint violations for blank fields");
    }

    @Test
    public void whenFieldsNull_thenViolations() {
        UserIntoAdminDTO dto = new UserIntoAdminDTO(
                null
        );

        Set<ConstraintViolation<UserIntoAdminDTO>> violations = validator.validate(dto);
        assertEquals(2, violations.size(), "Expected 2 constraint violations for null fields");
    }
}
