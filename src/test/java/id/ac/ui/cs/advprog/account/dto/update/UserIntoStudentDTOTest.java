package id.ac.ui.cs.advprog.account.dto.update;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserIntoStudentDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        UserIntoStudentDTO dto = new UserIntoStudentDTO();
        dto.role = "LECTURER";
        dto.username = "lecturer123";
        dto.fullName = "Dr. John Doe";
        dto.nim = "198702102019021001";

        Set<ConstraintViolation<UserIntoStudentDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    void whenFieldsAreBlank_thenViolationsOccur() {
        UserIntoStudentDTO dto = new UserIntoStudentDTO();
        dto.role = "";
        dto.username = "";
        dto.fullName = "";
        dto.nim = "";

        Set<ConstraintViolation<UserIntoStudentDTO>> violations = validator.validate(dto);
        assertEquals(4, violations.size(), "Expected 4 constraint violations");
    }

    @Test
    void whenSomeFieldsAreNull_thenViolationsOccur() {
        UserIntoStudentDTO dto = new UserIntoStudentDTO();
        dto.role = null;
        dto.username = null;
        dto.fullName = null;
        dto.nim = null;

        Set<ConstraintViolation<UserIntoStudentDTO>> violations = validator.validate(dto);
        assertEquals(4, violations.size(), "Expected 4 constraint violations");
    }
}
