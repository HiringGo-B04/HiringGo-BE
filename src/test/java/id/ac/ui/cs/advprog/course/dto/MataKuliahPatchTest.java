package id.ac.ui.cs.advprog.course.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MataKuliahPatchTest {

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        try (ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
            validator = vf.getValidator();
        }
    }

    /* ---------- VALID PATCH ---------- */
    @Test
    void patchWithPositiveSks_shouldPass() {
        MataKuliahPatch patch =
                new MataKuliahPatch(4, null, List.of(UUID.randomUUID()));

        assertThat(validator.validate(patch)).isEmpty();
    }

    /* ---------- SKS < 1 ---------- */
    @Test
    void patchWithZeroOrNegativeSks_shouldFail() {
        MataKuliahPatch patch =
                new MataKuliahPatch(0, "desc", null);   // 0 melanggar @Min(1)

        Set<ConstraintViolation<MataKuliahPatch>> violations = validator.validate(patch);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("sks");
    }

    /* ---------- ALL NULL (no changes) ---------- */
    @Test
    void patchWithAllNullFields_isAllowed() {
        MataKuliahPatch patch = new MataKuliahPatch(null, null, null);

        assertThat(validator.validate(patch)).isEmpty();
    }
}
