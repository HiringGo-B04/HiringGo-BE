package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;

import java.util.List;
import java.util.Optional;

/** CATATAN
 * Abstraksi repository Mata Kuliah – dipakai oleh Service layer.
 * <p>
 * Implementasi nyata:
 * <ul>
 *   <li><b>JpaMataKuliahRepository</b> &nbsp;: {@code @Profile("!test")} — persist ke PostgreSQL</li>
 *   <li><b>InMemoryMataKuliahRepository</b> : {@code @Profile("test")}  — unit‑test tanpa DB</li>
 * </ul>
 */
public interface MataKuliahRepository {

    /* ---------- CREATE / UPDATE ---------- */

    MataKuliah save(MataKuliah mk);
    default MataKuliah update(MataKuliah mk) {
        return save(mk);
    }

    /* ---------- READ ---------- */

    List<MataKuliah> findAll();
    Optional<MataKuliah> findByKode(String kode);

    /* ---------- DELETE ---------- */

    void deleteByKode(String kode);
}
