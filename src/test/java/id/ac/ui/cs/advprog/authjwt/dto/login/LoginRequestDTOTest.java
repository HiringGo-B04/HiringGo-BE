package id.ac.ui.cs.advprog.authjwt.dto.login;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAllFieldsValid_thenNoViolations() {
        LoginRequestDTO dto = new LoginRequestDTO(
                "user@example.com",
                "securePassword"
        );

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    public void whenFieldsBlank_thenViolations() {
        LoginRequestDTO dto = new LoginRequestDTO(
                "",
                ""
        );

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertEquals(2, violations.size(), "Expected 4 constraint violations for blank fields");
    }

    @Test
    public void whenFieldsNull_thenViolations() {
        LoginRequestDTO dto = new LoginRequestDTO(
                null,
                null
        );

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertEquals(4, violations.size(), "Expected 4 constraint violations for null fields");
    }
}
