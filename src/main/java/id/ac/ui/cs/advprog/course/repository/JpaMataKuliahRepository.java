package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** CATATAN
 * Implementasi repository berbasis Spring Data JPA.
 * Aktif di semua profile <b>kecuali</b> "test" — sehingga unit‑test tetap memakai
 * {@link InMemoryMataKuliahRepository}.
 */
@Repository
@Profile({"!test","jpa-test"})
public interface JpaMataKuliahRepository
        extends JpaRepository<MataKuliah, String>,
        MataKuliahRepository {
}
