package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryMataKuliahRepositoryTest {

    private MataKuliahRepository repository;

    @BeforeEach
    public void setUp() {
        // Menggunakan implementasi in-memory untuk testing
        repository = new InMemoryMataKuliahRepository();
    }

    // Test simpan data dan ambil semua data
    @Test
    public void testSaveAndFindAll() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma", "Deskripsi Mata Kuliah", 3);
        repository.save(mk);
        List<MataKuliah> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("IF1234", all.get(0).getKode());
    }

    // Test penyimpanan duplikat harus melempar exception
    @Test
    public void testSaveDuplicateThrowsException() {
        MataKuliah mk1 = new MataKuliah("IF1234", "Algoritma", "Deskripsi", 3);
        MataKuliah mk2 = new MataKuliah("IF1234", "Struktur Data", "Deskripsi Lain", 4);
        repository.save(mk1);
        Exception exception = assertThrows(RuntimeException.class, () -> repository.save(mk2));
        assertTrue(exception.getMessage().contains("Kode mata kuliah sudah terdaftar"));
    }

    // Test pencarian berdasarkan kode
    @Test
    public void testFindByKode() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma", "Deskripsi Mata Kuliah", 3);
        repository.save(mk);
        Optional<MataKuliah> found = repository.findByKode("IF1234");
        assertTrue(found.isPresent());
        assertEquals("Algoritma", found.get().getNama());
    }

    // Test update data mata kuliah yang sudah tersimpan
    @Test
    public void testUpdate() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma", "Deskripsi", 3);
        repository.save(mk);
        mk.setNama("Algoritma Updated");
        repository.update(mk);
        Optional<MataKuliah> updated = repository.findByKode("IF1234");
        assertTrue(updated.isPresent());
        assertEquals("Algoritma Updated", updated.get().getNama());
    }

    // Test update pada data yang belum tersimpan harus melempar exception
    @Test
    public void testUpdateNonExistentThrowsException() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma", "Deskripsi", 3);
        Exception exception = assertThrows(RuntimeException.class, () -> repository.update(mk));
        assertTrue(exception.getMessage().contains("Mata kuliah tidak ditemukan"));
    }

    // Test hapus data berdasarkan kode
    @Test
    public void testDeleteByKode() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma", "Deskripsi", 3);
        repository.save(mk);
        repository.deleteByKode("IF1234");
        Optional<MataKuliah> deleted = repository.findByKode("IF1234");
        assertFalse(deleted.isPresent());
    }
}
