package id.ac.ui.cs.advprog.course.mapper;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.mapstruct.*;

import java.util.*;

/** CATATAN
 * MapStruct mapper — konversi Entity ⇄ DTO untuk modul Mata Kuliah.
 *
 * ⚠️ Saat ini koleksi <code>dosenPengampu</code> pada DTO berisi <code>List&lt;String&gt;</code>
 * (mis. nama/UUID dosen). Karena kita belum memiliki service lookup <code>User</code> ←→ string,
 * konversi DTO → Entity hanya me‑return <code>emptySet()</code>. Logic pengisian relasi dosen
 * sebaiknya dilakukan di Service layer ketika sudah tersedia user‑repository.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy           = ReportingPolicy.IGNORE
)
public interface MataKuliahMapper {

    /* ---------- Manual converters ---------- */

    /** (JANGAN LUPA) Entity → DTO : ubah Set&lt;User&gt; menjadi List&lt;String&gt; (fullname). */
    default List<String> map(Set<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream()
                .map(User::getFullName)               // (JANGAN LUPA) pakai fullname; bisa diganti getUserId().toString()
                .toList();
    }

    /** (JANGAN LUPA)DTO → Entity : belum ada lookup User, kembalikan set kosong. */
    default Set<User> map(List<String> names) {
        // Belum ada lookup User → biarkan MapStruct meng‑abaikan kolom ini
        return null;          // <‑‑ perbaikan: kembalikan null
    }

    /* ---------- Mapping utama ---------- */

    MataKuliahDto toDto(MataKuliah entity);
    MataKuliah    toEntity(MataKuliahDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patch(MataKuliahPatch patch, @MappingTarget MataKuliah entity);
}
