package id.ac.ui.cs.advprog.authjwt.dto.register;

import id.ac.ui.cs.advprog.authjwt.dto.registration.LecturerRegistrationDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LecturerRegistrationDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAllFieldsValid_thenNoViolations() {
        LecturerRegistrationDTO dto = new LecturerRegistrationDTO(
                "lecturer1",
                "securePass123",
                "12345678",
                "John Doe"
        );

        Set<ConstraintViolation<LecturerRegistrationDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    public void whenFieldsBlank_thenViolations() {
        LecturerRegistrationDTO dto = new LecturerRegistrationDTO(
                "",
                "",
                "",
                ""
        );

        Set<ConstraintViolation<LecturerRegistrationDTO>> violations = validator.validate(dto);
        assertEquals(4, violations.size(), "Expected 4 constraint violations for blank fields");
    }

    @Test
    public void whenFieldsNull_thenViolations() {
        LecturerRegistrationDTO dto = new LecturerRegistrationDTO(
                null,
                null,
                null,
                null
        );

        Set<ConstraintViolation<LecturerRegistrationDTO>> violations = validator.validate(dto);
        assertEquals(8, violations.size(), "Expected 4 constraint violations for null fields");
    }
}
