package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit‑test untuk {@link InMemoryMataKuliahRepository} – versi baru
 * yang memakai return value pada save/update.
 */
class InMemoryMataKuliahRepositoryTest {

    private MataKuliahRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryMataKuliahRepository();   // @Primary tak berpengaruh di unit‑test
    }

    /* ---------- CREATE & FINDALL ---------- */
    @Test
    void save_shouldStoreEntityAndFindAllReturnList() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma", "Desc", 3);

        MataKuliah saved = repo.save(mk);            // kini mengembalikan entity
        assertSame(mk, saved);                       // referensi sama

        List<MataKuliah> all = repo.findAll();
        assertEquals(1, all.size());
        assertEquals("IF1234", all.get(0).getKode());
    }

    /* ---------- DUPLICATE SAVE ---------- */
    @Test
    void saveDuplicate_shouldThrowException() {
        repo.save(new MataKuliah("IF1234", "Algoritma", "Desc", 3));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                repo.save(new MataKuliah("IF1234", "Struktur Data", "Desc", 4)));

        assertTrue(ex.getMessage().contains("Kode mata kuliah sudah terdaftar"));
    }

    /* ---------- FINDBYKODE ---------- */
    @Test
    void findByKode_shouldReturnOptional() {
        repo.save(new MataKuliah("IF1234", "Algoritma", "Desc", 3));

        Optional<MataKuliah> found = repo.findByKode("IF1234");
        assertTrue(found.isPresent());
        assertEquals("Algoritma", found.get().getNama());
    }

    /* ---------- UPDATE EXISTING ---------- */
    @Test
    void updateExisting_shouldReplaceData() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma", "Desc", 3);
        repo.save(mk);

        mk.setNama("Algoritma Updated");
        MataKuliah updated = repo.update(mk);        // return entity

        assertEquals("Algoritma Updated", updated.getNama());
        assertEquals("Algoritma Updated", repo.findByKode("IF1234").get().getNama());
    }

    /* ---------- UPDATE NON‑EXISTENT ---------- */
    @Test
    void updateNonExistent_shouldThrowException() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma", "Desc", 3);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.update(mk));
        assertTrue(ex.getMessage().contains("Mata kuliah tidak ditemukan"));
    }

    /* ---------- DELETE ---------- */
    @Test
    void deleteByKode_shouldRemoveEntity() {
        repo.save(new MataKuliah("IF1234", "Algoritma", "Desc", 3));

        repo.deleteByKode("IF1234");
        assertFalse(repo.findByKode("IF1234").isPresent());
    }
}
