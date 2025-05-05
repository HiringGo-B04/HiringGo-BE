package id.ac.ui.cs.advprog.course.mapper;

import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit‑tests untuk memastikan konversi dan patch MapStruct
 * pada {@link MataKuliahMapper}.
 */
class MataKuliahMapperTest {

    private final MataKuliahMapper mapper = Mappers.getMapper(MataKuliahMapper.class);

    /* ---------- ENTITY → DTO ---------- */
    @Test
    void toDto_shouldCopyAllFields() {
        MataKuliah entity = new MataKuliah("MK001", "Algoritma",
                "Desc", 3);
        entity.setDosenPengampu(List.of("Dosen A"));
        MataKuliahDto dto = mapper.toDto(entity);

        assertThat(dto.kode()).isEqualTo(entity.getKode());
        assertThat(dto.nama()).isEqualTo(entity.getNama());
        assertThat(dto.sks()).isEqualTo(entity.getSks());
        assertThat(dto.deskripsi()).isEqualTo(entity.getDeskripsi());
        assertThat(dto.dosenPengampu()).containsExactly("Dosen A");
    }

    /* ---------- DTO → ENTITY ---------- */
    @Test
    void toEntity_shouldCopyAllFields() {
        MataKuliahDto dto = new MataKuliahDto(
                "MK002", "Basis Data", 4, "RDBMS", List.of("Dosen B"));
        MataKuliah entity = mapper.toEntity(dto);

        assertThat(entity.getKode()).isEqualTo(dto.kode());
        assertThat(entity.getNama()).isEqualTo(dto.nama());
        assertThat(entity.getSks()).isEqualTo(dto.sks());
        assertThat(entity.getDeskripsi()).isEqualTo(dto.deskripsi());
        assertThat(entity.getDosenPengampu()).containsExactly("Dosen B");
    }

    /* ---------- PATCH ---------- */
    @Test
    void patch_shouldUpdateNonNullFieldsOnly() {
        // entity sebelum di‑patch
        MataKuliah entity = new MataKuliah("MK003", "Jaringan",
                "Old Desc", 2);
        // patch hanya ubah sks
        MataKuliahPatch patch = new MataKuliahPatch(5, null);

        mapper.patch(patch, entity);

        // sks ter‑update
        assertThat(entity.getSks()).isEqualTo(5);
        // deskripsi tetap sama (karena null di patch)
        assertThat(entity.getDeskripsi()).isEqualTo("Old Desc");
    }
}
