package id.ac.ui.cs.advprog.account.dto.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.account.dto.update.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserUpdateDTOTest {

    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidationFailsOnBlankFields_Student() {
        UserIntoStudentDTO dto = new UserIntoStudentDTO("", "", "", "");

        Set<ConstraintViolation<UserIntoStudentDTO>> violations = validator.validate(dto);
        assertEquals(4, violations.size());
    }

    @Test
    void testValidationFailsOnBlankFields_Lecturer() {
        UserIntoLecturerDTO dto = new UserIntoLecturerDTO("", "", "", "");

        Set<ConstraintViolation<UserIntoLecturerDTO>> violations = validator.validate(dto);
        assertEquals(4, violations.size());
    }

    @Test
    void testValidationFailsOnBlankFields_Admin() {
        UserIntoAdminDTO dto = new UserIntoAdminDTO("", "");

        Set<ConstraintViolation<UserIntoAdminDTO>> violations = validator.validate(dto);
        assertEquals(2, violations.size());
    }

    @Test
    void testValidationPassesOnValidInput_Student() {
        UserIntoStudentDTO dto = new UserIntoStudentDTO("Jane Doe", "123456", "jane", "STUDENT");

        Set<ConstraintViolation<UserIntoStudentDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidationPassesOnValidInput_Lecturer() {
        UserIntoLecturerDTO dto = new UserIntoLecturerDTO("Dr. Smith", "654321", "smith", "LECTURER");

        Set<ConstraintViolation<UserIntoLecturerDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidationPassesOnValidInput_Admin() {
        UserIntoAdminDTO dto = new UserIntoAdminDTO("ADMIN", "admin");

        Set<ConstraintViolation<UserIntoAdminDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
