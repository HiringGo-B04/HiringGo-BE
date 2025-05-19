package id.ac.ui.cs.advprog.account.dto.get;

import id.ac.ui.cs.advprog.authjwt.model.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetAllUserDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAllFieldsValid_thenNoViolations() {
        List<User> users = Collections.emptyList();
        GetAllUserDTO dto = new GetAllUserDTO(
                "success", "Users retrieved successfully",
                10, 20, 5, 3,
                users
        );

        Set<ConstraintViolation<GetAllUserDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    public void whenStatusAndMessageBlank_thenViolations() {
        GetAllUserDTO dto = new GetAllUserDTO(
                "", "", 10, 10, 10, 10, Collections.emptyList()
        );

        Set<ConstraintViolation<GetAllUserDTO>> violations = validator.validate(dto);
        assertEquals(2, violations.size(), "Expected 2 constraint violations for blank fields");
    }
}
