package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit‑test CRUD untuk {@link InMemoryMataKuliahRepository}.
 */
class InMemoryMataKuliahRepositoryTest {

    private MataKuliahRepository repo;

    /* ---------- Helper ---------- */
    private MataKuliah mk(String kode, String nama, int sks) {
        return new MataKuliah(kode, nama, "Desc", sks, new HashSet<>());
    }

    @BeforeEach
    void setUp() {
        repo = new InMemoryMataKuliahRepository();   // profile tidak berpengaruh di unit‑test
    }

    /* ---------- CREATE & FINDALL ---------- */
    @Test
    void save_shouldStoreEntityAndFindAllReturnList() {
        MataKuliah matkul = mk("IF1234", "Algoritma", 3);

        MataKuliah saved = repo.save(matkul);
        assertSame(matkul, saved);

        List<MataKuliah> all = repo.findAll();
        assertEquals(1, all.size());
        assertEquals("IF1234", all.getFirst().getKode());
    }

    /* ---------- DUPLICATE SAVE ---------- */
    @Test
    void saveDuplicate_shouldThrowException() {
        repo.save(mk("IF1234", "Algoritma", 3));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> repo.save(mk("IF1234", "Struktur Data", 4)));

        assertTrue(ex.getMessage().contains("Kode mata kuliah sudah terdaftar"));
    }

    /* ---------- FINDBYKODE ---------- */
    @Test
    void findByKode_shouldReturnOptional() {
        repo.save(mk("IF1234", "Algoritma", 3));

        Optional<MataKuliah> found = repo.findByKode("IF1234");
        assertTrue(found.isPresent());
        assertEquals("Algoritma", found.get().getNama());
    }

    /* ---------- UPDATE EXISTING ---------- */
    @Test
    void updateExisting_shouldReplaceData() {
        MataKuliah matkul = mk("IF1234", "Algoritma", 3);
        repo.save(matkul);

        matkul.setNama("Algoritma Updated");
        MataKuliah updated = repo.update(matkul);

        assertEquals("Algoritma Updated", updated.getNama());
        assertEquals("Algoritma Updated", repo.findByKode("IF1234").get().getNama());
    }

    /* ---------- UPDATE NON‑EXISTENT ---------- */
    @Test
    void updateNonExistent_shouldThrowException() {
        MataKuliah matkul = mk("IF9999", "Non‑Exist", 2);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> repo.update(matkul));

        assertTrue(ex.getMessage().contains("Mata kuliah tidak ditemukan"));
    }

    /* ---------- DELETE ---------- */
    @Test
    void deleteByKode_shouldRemoveEntity() {
        repo.save(mk("IF1234", "Algoritma", 3));

        repo.deleteByKode("IF1234");
        assertFalse(repo.findByKode("IF1234").isPresent());
    }
}
