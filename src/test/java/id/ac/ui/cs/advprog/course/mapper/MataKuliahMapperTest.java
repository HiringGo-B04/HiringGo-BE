package id.ac.ui.cs.advprog.course.mapper;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit‑tests konversi MapStruct pada {@link MataKuliahMapper}.
 */
class MataKuliahMapperTest {

    private final MataKuliahMapper mapper = Mappers.getMapper(MataKuliahMapper.class);

    private User dummyLecturer(String fullname) {
        User u = new User(UUID.randomUUID(), fullname.toLowerCase() + "@mail.com", "pw");
        u.setFullName(fullname);
        return u;
    }

    /* ---------- ENTITY → DTO ---------- */
    @Test
    void toDto_shouldCopyAllFieldsAndLecturers() {
        User dosenA = dummyLecturer("Dosen A");
        MataKuliah entity = new MataKuliah(
                "MK001", "Algoritma", "Desc", 3,
                new HashSet<>(Set.of(dosenA))
        );

        MataKuliahDto dto = mapper.toDto(entity);

        assertThat(dto.kode()).isEqualTo("MK001");
        assertThat(dto.dosenPengampu()).containsExactly("Dosen A");
    }

    /* ---------- DTO → ENTITY ---------- */
    @Test
    void toEntity_shouldIgnoreDosenPengampuList() {
        MataKuliahDto dto = new MataKuliahDto(
                "MK002", "Basis Data", 4, "RDBMS",
                List.of("Dosen B")
        );

        MataKuliah entity = mapper.toEntity(dto);

        assertThat(entity.getKode()).isEqualTo("MK002");
        // mapper meng‑return null (akan di‑inisialisasi service)
        assertThat(entity.getDosenPengampu()).isNull();
    }

    /* ---------- PATCH ---------- */
    @Test
    void patch_shouldUpdateNonNullFieldsOnly() {
        MataKuliah entity = new MataKuliah(
                "MK003", "Jaringan", "Old", 2, new HashSet<>());

        MataKuliahPatch patch = new MataKuliahPatch(5, null);
        mapper.patch(patch, entity);

        assertThat(entity.getSks()).isEqualTo(5);
        assertThat(entity.getDeskripsi()).isEqualTo("Old");
    }
}
