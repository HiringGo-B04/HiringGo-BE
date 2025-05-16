//package id.ac.ui.cs.advprog.account.dto.delete;
//
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.Validation;
//import jakarta.validation.Validator;
//import jakarta.validation.ValidatorFactory;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import java.util.Locale;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class DeleteResponseDTOTest {
//
//    private static Validator validator;
//
//    @BeforeAll
//    public static void setupValidatorInstance() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//    }
//
//    @Test
//    public void whenAllFieldsValid_thenNoViolations() {
//        DeleteResponseDTO dto = new DeleteResponseDTO(
//                "abcd",
//                "abcd"
//        );
//
//        Set<ConstraintViolation<DeleteResponseDTO>> violations = validator.validate(dto);
//        assertTrue(violations.isEmpty(), "Expected no constraint violations");
//    }
//
//    @Test
//    public void whenFieldsBlank_thenViolations() {
//        DeleteResponseDTO dto = new DeleteResponseDTO(
//                "",
//                ""
//        );
//
//        Set<ConstraintViolation<DeleteResponseDTO>> violations = validator.validate(dto);
//        assertEquals(2, violations.size(), "Expected 2 constraint violations for blank fields");
//    }
//
//    @Test
//    public void whenFieldsNull_thenViolations() {
//        DeleteResponseDTO dto = new DeleteResponseDTO(
//                null,
//                null
//        );
//
//        Set<ConstraintViolation<DeleteResponseDTO>> violations = validator.validate(dto);
//        assertEquals(4, violations.size(), "Expected 2 constraint violations for null fields");
//    }
//}
