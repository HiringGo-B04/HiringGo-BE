package id.ac.ui.cs.advprog.authjwt.dto.register;

import id.ac.ui.cs.advprog.authjwt.dto.registration.AdminRegistrationDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AdminRegistrationDTOTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        // Initialize the validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testAdminRegistrationDTO_Valid() {
        // Valid DTO with non-null and non-empty username and password
        AdminRegistrationDTO dto = new AdminRegistrationDTO("admin@gmail.com", "password123");

        Set<jakarta.validation.ConstraintViolation<AdminRegistrationDTO>> violations = validator.validate(dto);

        // There should be no violations
        assertTrue(violations.isEmpty(), "There should be no validation errors.");
    }

    @Test
    public void testAdminRegistrationDTO_UsernameNull() {
        // Invalid DTO where username is null
        AdminRegistrationDTO dto = new AdminRegistrationDTO(null, "password123");

        Set<jakarta.validation.ConstraintViolation<AdminRegistrationDTO>> violations = validator.validate(dto);

        // There should be one violation for username being null
        assertFalse(violations.isEmpty(), "There should be validation errors.");
        assertEquals(2, violations.size(), "There should be exactly two validation error.");
    }

    @Test
    public void testAdminRegistrationDTO_UsernameBlank() {
        // Invalid DTO where username is empty
        AdminRegistrationDTO dto = new AdminRegistrationDTO("", "password123");

        Set<jakarta.validation.ConstraintViolation<AdminRegistrationDTO>> violations = validator.validate(dto);

        // There should be one violation for username being blank
        assertFalse(violations.isEmpty(), "There should be validation errors.");
        assertEquals(1, violations.size(), "There should be exactly one validation error.");
    }

    @Test
    public void testAdminRegistrationDTO_PasswordNull() {
        // Invalid DTO where password is null
        AdminRegistrationDTO dto = new AdminRegistrationDTO("admin@gmail.com", null);

        Set<jakarta.validation.ConstraintViolation<AdminRegistrationDTO>> violations = validator.validate(dto);

        // There should be one violation for password being null
        assertFalse(violations.isEmpty(), "There should be validation errors.");
        assertEquals(2, violations.size(), "There should be exactly two validation error.");
    }

    @Test
    public void testAdminRegistrationDTO_PasswordBlank() {
        // Invalid DTO where password is empty
        AdminRegistrationDTO dto = new AdminRegistrationDTO("admin@gmail.com", "");

        Set<jakarta.validation.ConstraintViolation<AdminRegistrationDTO>> violations = validator.validate(dto);

        // There should be one violation for password being blank
        assertFalse(violations.isEmpty(), "There should be validation errors.");
        assertEquals(1, violations.size(), "There should be exactly one validation error.");
    }

    @Test
    public void testAdminRegistrationDTO_PasswordUsernameBlank() {
        // Invalid DTO where password is empty
        AdminRegistrationDTO dto = new AdminRegistrationDTO("", "");

        Set<jakarta.validation.ConstraintViolation<AdminRegistrationDTO>> violations = validator.validate(dto);

        // There should be one violation for password being blank
        assertFalse(violations.isEmpty(), "There should be validation errors.");
        assertEquals(2, violations.size(), "There should be exactly two validation error.");
    }

    @Test
    public void testAdminRegistrationDTO_PasswordUsernameNull() {
        // Invalid DTO where password is empty
        AdminRegistrationDTO dto = new AdminRegistrationDTO(null, null);

        Set<jakarta.validation.ConstraintViolation<AdminRegistrationDTO>> violations = validator.validate(dto);

        // There should be one violation for password being blank
        assertFalse(violations.isEmpty(), "There should be validation errors.");
        assertEquals(4, violations.size(), "There should be exactly four validation error.");
    }
}
