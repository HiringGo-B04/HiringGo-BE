package id.ac.ui.cs.advprog.course.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit‑tests Bean Validation untuk {@link MataKuliahPatch}.
 */
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
        MataKuliahPatch patch = new MataKuliahPatch(4, null);

        Set<ConstraintViolation<MataKuliahPatch>> violations = validator.validate(patch);
        assertThat(violations).isEmpty();
    }

    /* ---------- NEGATIVE SKS ---------- */
    @Test
    void patchWithNegativeSks_shouldFail() {
        MataKuliahPatch patch = new MataKuliahPatch(-2, "desc");

        Set<ConstraintViolation<MataKuliahPatch>> violations = validator.validate(patch);
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("sks");
    }

    /* ---------- ALL NULL (no changes) ---------- */
    @Test
    void patchWithAllNullFields_isAllowed() {
        MataKuliahPatch patch = new MataKuliahPatch(null, null);

        Set<ConstraintViolation<MataKuliahPatch>> violations = validator.validate(patch);
        assertThat(violations).isEmpty();
    }
}
