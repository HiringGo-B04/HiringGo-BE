package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("jpa-test")
class JpaMataKuliahRepositoryTest {

    @Autowired
    private JpaMataKuliahRepository repo;

    /* ---------- Helper ---------- */
    private MataKuliah mk(String kode, String nama, int sks) {
        return new MataKuliah(kode, nama, null, sks);
    }

    /* ---------- Create & Read ---------- */
    @Test
    @DisplayName("saveAndFlush lalu findById mengembalikan entitas")
    void save_thenFindById_shouldReturnEntity() {
        repo.saveAndFlush(mk("IF0001", "Algoritma", 3));

        Optional<MataKuliah> found = repo.findById("IF0001");
        assertTrue(found.isPresent());
        assertEquals("Algoritma", found.get().getNama());
    }

    /* ---------- FindAll ---------- */
    @Test
    @DisplayName("findAll memuat semua entitas")
    void findAll_shouldReturnAllEntities() {
        repo.saveAndFlush(mk("IF0002", "Logika", 2));
        repo.saveAndFlush(mk("IF0003", "Basis Data", 3));

        assertEquals(2, repo.findAll().size());
    }

    /* ---------- Delete ---------- */
    @Test
    @DisplayName("deleteById menghapus entitas")
    void deleteById_shouldRemoveEntity() {
        repo.saveAndFlush(mk("IF0004", "Jaringan", 3));

        repo.deleteById("IF0004");

        assertFalse(repo.findById("IF0004").isPresent());
    }

    /* ---------- Unique constraint ---------- */
    @Test
    @DisplayName("kode duplikat menimbulkan DataIntegrityViolationException")
    void saveDuplicateKode_shouldThrowException() {
        repo.saveAndFlush(mk("IF0005", "Pemrograman 1", 3));

        assertThrows(DataIntegrityViolationException.class,
                () -> repo.saveAndFlush(mk("IF0005", "Pemrograman 1 Revisi", 4)));
    }

    /* ---------- Validation: null PK ---------- */
    @Test
    @DisplayName("menyimpan entitas tanpa kode gagal (null PK)")
    void saveWithoutKode_shouldFailValidation() {
        MataKuliah invalid = mk(null, "Tanpa Kode", 2);

        assertThrows(ConstraintViolationException.class,
                () -> repo.saveAndFlush(invalid));
    }
}
