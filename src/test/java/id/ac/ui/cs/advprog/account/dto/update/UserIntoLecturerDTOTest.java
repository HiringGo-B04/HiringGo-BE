package id.ac.ui.cs.advprog.account.dto.update;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserIntoLecturerDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        UserIntoLecturerDTO dto = new UserIntoLecturerDTO();
        dto.role = "LECTURER";
        dto.username = "lecturer123";
        dto.fullName = "Dr. John Doe";
        dto.nip = "198702102019021001";

        Set<ConstraintViolation<UserIntoLecturerDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    void whenFieldsAreBlank_thenViolationsOccur() {
        UserIntoLecturerDTO dto = new UserIntoLecturerDTO();
        dto.role = "";
        dto.username = "";
        dto.fullName = "";
        dto.nip = "";

        Set<ConstraintViolation<UserIntoLecturerDTO>> violations = validator.validate(dto);
        assertEquals(4, violations.size(), "Expected 4 constraint violations");
    }

    @Test
    void whenSomeFieldsAreNull_thenViolationsOccur() {
        UserIntoLecturerDTO dto = new UserIntoLecturerDTO();
        dto.role = null;
        dto.username = null;
        dto.fullName = null;
        dto.nip = null;

        Set<ConstraintViolation<UserIntoLecturerDTO>> violations = validator.validate(dto);
        assertEquals(4, violations.size(), "Expected 4 constraint violations");
    }
}
