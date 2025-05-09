package id.ac.ui.cs.advprog.authjwt.dto.logout;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogoutRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAllFieldsValid_thenNoViolations() {
        LogoutRequestDTO dto = new LogoutRequestDTO(
                "user@example.com"
        );

        Set<ConstraintViolation<LogoutRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    public void whenFieldsBlank_thenViolations() {
        LogoutRequestDTO dto = new LogoutRequestDTO(
                ""
        );

        Set<ConstraintViolation<LogoutRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Expected 1 constraint violations for blank fields");
    }

    @Test
    public void whenFieldsNull_thenViolations() {
        LogoutRequestDTO dto = new LogoutRequestDTO(
                null
        );

        Set<ConstraintViolation<LogoutRequestDTO>> violations = validator.validate(dto);
        assertEquals(2, violations.size(), "Expected 2 constraint violations for null fields");
    }
}
