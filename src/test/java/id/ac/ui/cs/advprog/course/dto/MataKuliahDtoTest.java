package id.ac.ui.cs.advprog.course.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit‑test sederhana untuk memastikan anotasi Bean Validation
 * pada {@link MataKuliahDto} berfungsi.
 */
class MataKuliahDtoTest {

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        try (ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
            validator = vf.getValidator();
        }
    }

    /* ---------- HAPPY PATH ---------- */
    @Test
    void validDto_shouldHaveNoViolations() {
        MataKuliahDto dto = new MataKuliahDto(
                "MK001",
                "Algoritma",
                3,
                "Pemrograman dasar algoritma",
                List.of("Dosen A", "Dosen B")
        );

        Set<ConstraintViolation<MataKuliahDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    /* ---------- NEGATIVE SKS ---------- */
    @Test
    void negativeSks_shouldFailValidation() {
        MataKuliahDto dto = new MataKuliahDto(
                "MK002",
                "Basis Data",
                -1,
                "Desc",
                List.of()
        );

        Set<ConstraintViolation<MataKuliahDto>> violations = validator.validate(dto);
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(p -> p.toString().equals("sks"));
    }

    /* ---------- KODE / NAMA BLANK ---------- */
    @Test
    void blankKodeOrNama_shouldFailValidation() {
        MataKuliahDto dto = new MataKuliahDto(
                "   ",      // kode blank
                "",
                2,
                null,
                List.of("Dosen X")
        );

        Set<ConstraintViolation<MataKuliahDto>> violations = validator.validate(dto);
        assertThat(violations).hasSizeGreaterThanOrEqualTo(2)
                .extracting(ConstraintViolation::getPropertyPath)
                .anySatisfy(path -> {
                    assertThat(path.toString()).isIn("kode", "nama");
                });
    }

    /* ---------- DOSEN LIST NULL ---------- */
    @Test
    void nullDosenPengampu_shouldFailValidation() {
        MataKuliahDto dto = new MataKuliahDto(
                "MK003",
                "Jaringan",
                3,
                null,
                null               // ← tidak boleh null
        );

        Set<ConstraintViolation<MataKuliahDto>> violations = validator.validate(dto);
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(p -> p.toString().equals("dosenPengampu"));
    }
}
