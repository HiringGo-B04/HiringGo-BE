package id.ac.ui.cs.advprog.authjwt.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StudentRegistrationDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAllFieldsValid_thenNoViolations() {
        StudentRegistrationDTO dto = new StudentRegistrationDTO(
                "student1",
                "strongPassword123",
                "2106754321",
                "Jane Smith"
        );

        Set<ConstraintViolation<StudentRegistrationDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    public void whenFieldsBlank_thenViolations() {
        StudentRegistrationDTO dto = new StudentRegistrationDTO(
                "",
                "",
                "",
                ""
        );

        Set<ConstraintViolation<StudentRegistrationDTO>> violations = validator.validate(dto);
        assertEquals(4, violations.size(), "Expected 4 constraint violations for blank fields");
    }

    @Test
    public void whenFieldsNull_thenViolations() {
        StudentRegistrationDTO dto = new StudentRegistrationDTO(
                null,
                null,
                null,
                null
        );

        Set<ConstraintViolation<StudentRegistrationDTO>> violations = validator.validate(dto);
        assertEquals(8, violations.size(), "Expected 4 constraint violations for null fields");
    }
}
