package id.ac.ui.cs.advprog.course.mapper;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

/**
 * MapStruct mapper — konversi Entity ⇄ DTO untuk modul Mata Kuliah.
 * • Entity → DTO : Set<User>  → List<UUID>   (ambil userId)
 * • DTO    → Entity : List<UUID> → Set<User>  (lookup ke UserRepository)
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy           = ReportingPolicy.IGNORE
)
public abstract class MataKuliahMapper {

    /* ---------- Dependency lookup ---------- */
    @Autowired
    protected UserRepository userRepository;

    /* ---------- Mapping utama ---------- */

    @Mapping(source = "dosenPengampu", target = "dosenPengampu")
    public abstract MataKuliahDto toDto(MataKuliah entity);

    @Mapping(source = "dosenPengampu", target = "dosenPengampu")
    public abstract MataKuliah toEntity(MataKuliahDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patch(MataKuliahPatch patch, @MappingTarget MataKuliah entity);

    /* ---------- Custom converters ---------- */

    /** Entity → DTO : Set<User> → List<UUID>. */
    protected List<UUID> map(Set<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream()
                .map(User::getUserId)         // asumsi field 'userId' bertipe UUID
                .collect(Collectors.toList());
    }

    /** DTO → Entity : List<UUID> → Set<User>. */
    protected Set<User> map(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptySet();
        }
        // UserRepository#findAllById mengembalikan Iterable<User>
        Iterable<User> fetched = userRepository.findAllById(ids);
        Set<User> result = new HashSet<>();
        fetched.forEach(result::add);
        return result;
    }
}
