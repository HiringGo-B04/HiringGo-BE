package id.ac.ui.cs.advprog.course.service;

import id.ac.ui.cs.advprog.course.repository.InMemoryMataKuliahRepository;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MataKuliahServiceImplTest {

    private MataKuliahRepository repository;
    private MataKuliahService service;

    @BeforeEach
    public void setUp() {
        // Inisialisasi repository in-memory dan injeksikan ke service
        repository = new InMemoryMataKuliahRepository();
        service = new MataKuliahServiceImpl(repository);
    }

    // Test pembuatan mata kuliah dengan valid (create success)
    @Test
    void testCreateSuccess() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma", "Deskripsi Mata Kuliah", 3);
        service.create(mk);
        MataKuliah result = service.findByKode("IF1234");
        assertNotNull(result);
        assertEquals("Algoritma", result.getNama());
    }

    // Test pembuatan mata kuliah dengan kode yang sudah ada (duplicate)
    @Test
    void testCreateDuplicateThrowsException() {
        MataKuliah mk1 = new MataKuliah("IF1234", "Algoritma", "Deskripsi", 3);
        service.create(mk1);
        MataKuliah mk2 = new MataKuliah("IF1234", "Data Structures", "Deskripsi Lain", 3);
        Exception exception = assertThrows(RuntimeException.class, () -> service.create(mk2));
        assertTrue(exception.getMessage().contains("Kode mata kuliah sudah terdaftar"));
    }

    // Test validasi saat membuat: kode tidak boleh kosong dan SKS tidak boleh negatif
    @Test
    void testCreateInvalidMataKuliahThrowsException() {
        // Kode kosong
        MataKuliah mkInvalidKode = new MataKuliah("", "Nama", "Deskripsi", 3);
        Exception exception1 = assertThrows(RuntimeException.class, () -> service.create(mkInvalidKode));
        assertEquals("Kode mata kuliah tidak boleh kosong", exception1.getMessage());

        // SKS negatif
        MataKuliah mkInvalidSks = new MataKuliah("IF5678", "Nama", "Deskripsi", -1);
        Exception exception2 = assertThrows(RuntimeException.class, () -> service.create(mkInvalidSks));
        assertEquals("SKS tidak boleh negatif", exception2.getMessage());
    }

    // Test pengambilan semua mata kuliah (findAll)
    @Test
    void testFindAll() {
        MataKuliah mk1 = new MataKuliah("IF1234", "Algoritma", "Deskripsi", 3);
        MataKuliah mk2 = new MataKuliah("IF5678", "Data Structures", "Deskripsi", 4);
        service.create(mk1);
        service.create(mk2);
        List<MataKuliah> list = service.findAll();
        assertEquals(2, list.size());
    }

    // Test pencarian berdasarkan kode yang tidak ada (findByKode)
    @Test
    void testFindByKodeNotFound() {
        MataKuliah result = service.findByKode("NON_EXISTENT");
        assertNull(result);
    }

    // Test update data mata kuliah yang sudah ada
    @Test
    void testUpdateSuccess() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma", "Deskripsi", 3);
        service.create(mk);

        // Update nama
        mk.setNama("Algoritma Updated");
        service.update(mk);

        MataKuliah updated = service.findByKode("IF1234");
        assertNotNull(updated);
        assertEquals("Algoritma Updated", updated.getNama());
    }

    // Test update data yang tidak ada harus melempar exception
    @Test
    void testUpdateNonExistentThrowsException() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma", "Deskripsi", 3);
        Exception exception = assertThrows(RuntimeException.class, () -> service.update(mk));
        assertTrue(exception.getMessage().contains("Mata kuliah tidak ditemukan"));
    }

    // Test menghapus data mata kuliah yang sudah ada
    @Test
    void testDeleteSuccess() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma", "Deskripsi", 3);
        service.create(mk);
        service.delete("IF1234");
        MataKuliah result = service.findByKode("IF1234");
        assertNull(result);
    }

    // Test penghapusan data yang tidak ada harus melempar exception
    @Test
    void testDeleteNonExistentThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> service.delete("NON_EXISTENT"));
        assertTrue(exception.getMessage().contains("Mata kuliah tidak ditemukan"));
    }
}
