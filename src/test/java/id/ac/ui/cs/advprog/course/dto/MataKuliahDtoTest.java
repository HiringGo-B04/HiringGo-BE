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
                List.of(UUID.randomUUID())
        );

        assertThat(validator.validate(dto)).isEmpty();
    }

    /* ---------- SKS < 1 ---------- */
    @Test
    void zeroOrNegativeSks_shouldFailValidation() {
        MataKuliahDto dto = new MataKuliahDto(
                "MK002",
                "Basis Data",
                0,
                "Desc",
                List.of()
        );

        Set<ConstraintViolation<MataKuliahDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("sks");
    }

    /* ---------- KODE / NAMA BLANK ---------- */
    @Test
    void blankKodeOrNama_shouldFailValidation() {
        MataKuliahDto dto = new MataKuliahDto(
                "   ",
                "",
                2,
                null,
                List.of(UUID.randomUUID())
        );

        Set<ConstraintViolation<MataKuliahDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("kode", "nama");
    }

    /* ---------- DOSEN LIST NULL ---------- */
    @Test
    void nullDosenPengampu_shouldFailValidation() {
        MataKuliahDto dto = new MataKuliahDto(
                "MK003",
                "Jaringan",
                3,
                null,
                null
        );

        Set<ConstraintViolation<MataKuliahDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("dosenPengampu");
    }
}
