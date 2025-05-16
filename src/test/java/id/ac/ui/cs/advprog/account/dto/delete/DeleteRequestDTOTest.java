package id.ac.ui.cs.advprog.account.dto.delete;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeleteRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAllFieldsValid_thenNoViolations() {
        DeleteRequestDTO dto = new DeleteRequestDTO(
                "user@example.com"
        );

        Set<ConstraintViolation<DeleteRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    public void whenFieldsBlank_thenViolations() {
        DeleteRequestDTO dto = new DeleteRequestDTO(
                ""
        );

        Set<ConstraintViolation<DeleteRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Expected 1 constraint violations for blank fields");
    }

    @Test
    public void whenFieldsNull_thenViolations() {
        DeleteRequestDTO dto = new DeleteRequestDTO(
                null
        );

        Set<ConstraintViolation<DeleteRequestDTO>> violations = validator.validate(dto);
        assertEquals(2, violations.size(), "Expected 2 constraint violations for null fields");
    }
}
