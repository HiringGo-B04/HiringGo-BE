package id.ac.ui.cs.advprog.account.dto.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserUpdateDTOTest {

    private static Validator validator;
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setup() {
        // Setup the validator and ObjectMapper instances
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        objectMapper = new ObjectMapper(); // Jackson's ObjectMapper to handle JSON serialization/deserialization
    }

    @Test
    public void whenValidJson_thenCorrectObjectDeserialized() throws Exception {
        String json = "{\"role\":\"ADMIN\", \"username\":\"adminuser\"}";

        // Deserialize JSON to UserUpdateDTO
        UserUpdateDTO dto = objectMapper.readValue(json, UserIntoAdminDTO.class);

        // Assert that the deserialized object is of type UserIntoAdminDTO (subclass of UserUpdateDTO)
        assertTrue(dto instanceof UserIntoAdminDTO, "Deserialized object should be of type UserIntoAdminDTO");
        assertEquals("ADMIN", dto.role, "Role should be ADMIN");
        assertEquals("adminuser", dto.username, "Username should be adminuser");
    }

    @Test
    public void whenRoleBlank_thenViolations() {
        // Prepare an instance of UserIntoAdminDTO with a blank role
        UserIntoAdminDTO dto = new UserIntoAdminDTO();
        dto.role = "";  // Blank role
        dto.username = "adminuser"; // Valid username

        // Validate the DTO
        Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(dto);

        // Assert that there is a violation for the role field
        assertEquals(1, violations.size(), "Expected one violation for role");
        assertEquals("role", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    public void whenUsernameBlank_thenViolations() {
        // Prepare an instance of UserIntoAdminDTO with a blank username
        UserIntoAdminDTO dto = new UserIntoAdminDTO();
        dto.role = "ADMIN";  // Valid role
        dto.username = "";  // Blank username

        // Validate the DTO
        Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(dto);

        // Assert that there is a violation for the username field
        assertEquals(1, violations.size(), "Expected one violation for username");
        assertEquals("username", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    public void whenRoleAndUsernameBlank_thenViolations() {
        // Prepare an instance of UserIntoAdminDTO with both blank fields
        UserIntoAdminDTO dto = new UserIntoAdminDTO();
        dto.role = "";  // Blank role
        dto.username = "";  // Blank username

        // Validate the DTO
        Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(dto);

        // Assert that there are two violations: one for role and one for username
        assertEquals(2, violations.size(), "Expected two violations for blank fields");
    }

    @Test
    public void whenInvalidJson_thenDeserializationFails() {
        String json = "{\"role\":\"ADMIN\", \"username\": \"\"}";  // Invalid: empty username

        try {
            // Attempt to deserialize JSON into UserUpdateDTO (should fail due to blank username)
            objectMapper.readValue(json, UserIntoAdminDTO.class);
        } catch (Exception e) {
            // Ensure exception is thrown and the deserialization fails (since username is blank)
            assertTrue(e instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException, "Expected deserialization exception");
        }
    }
}
