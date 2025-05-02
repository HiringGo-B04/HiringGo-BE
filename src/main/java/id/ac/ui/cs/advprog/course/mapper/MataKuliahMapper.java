package id.ac.ui.cs.advprog.course.mapper;

import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper — konversi Entity ⇄ DTO serta
 * menerapkan patch (partial update) dengan strategi
 * “abaikan property yang null”.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MataKuliahMapper {

    /* ---------- Entity ⇄ DTO ---------- */

    MataKuliahDto toDto(MataKuliah entity);

    MataKuliah toEntity(MataKuliahDto dto);

    /* ---------- PATCH (partial update) ---------- */

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patch(MataKuliahPatch patch, @MappingTarget MataKuliah entity);
}
