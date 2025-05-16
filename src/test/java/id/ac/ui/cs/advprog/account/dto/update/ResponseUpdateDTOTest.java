package id.ac.ui.cs.advprog.account.dto.update;

import id.ac.ui.cs.advprog.account.dto.update.ResponseUpdateDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResponseUpdateDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAllFieldsValid_thenNoViolations() {
        ResponseUpdateDTO dto = new ResponseUpdateDTO(
                "abcd",
                "abcd"
        );

        Set<ConstraintViolation<ResponseUpdateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    public void whenFieldsBlank_thenViolations() {
        ResponseUpdateDTO dto = new ResponseUpdateDTO(
                "",
                ""
        );

        Set<ConstraintViolation<ResponseUpdateDTO>> violations = validator.validate(dto);
        assertEquals(2, violations.size(), "Expected 2 constraint violations for blank fields");
    }

    @Test
    public void whenFieldsNull_thenViolations() {
        ResponseUpdateDTO dto = new ResponseUpdateDTO(
                null,
                null
        );

        Set<ConstraintViolation<ResponseUpdateDTO>> violations = validator.validate(dto);
        assertEquals(4, violations.size(), "Expected 2 constraint violations for null fields");
    }
}
