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

public class UserIntoLecturerDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAllFieldsValid_thenNoViolations() {
        UserIntoLecturerDTO dto = new UserIntoLecturerDTO(
                "user@example.com",
                "abc",
                "123123123"
        );

        Set<ConstraintViolation<UserIntoLecturerDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    public void whenFieldsBlank_thenViolations() {
        UserIntoLecturerDTO dto = new UserIntoLecturerDTO(
                "",
                "",
                ""
        );

        Set<ConstraintViolation<UserIntoLecturerDTO>> violations = validator.validate(dto);
        assertEquals(3, violations.size(), "Expected 1 constraint violations for blank fields");
    }

    @Test
    public void whenFieldsNull_thenViolations() {
        UserIntoLecturerDTO dto = new UserIntoLecturerDTO(
                null,
                null,
                null
        );

        Set<ConstraintViolation<UserIntoLecturerDTO>> violations = validator.validate(dto);
        assertEquals(6, violations.size(), "Expected 2 constraint violations for null fields");
    }
}
