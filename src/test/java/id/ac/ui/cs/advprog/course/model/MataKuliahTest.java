package id.ac.ui.cs.advprog.course.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MataKuliahTest {

    // Test default constructor dan inisialisasi list dosenPengampu
    @Test
    public void testDefaultConstructor() {
        MataKuliah mk = new MataKuliah();
        // id belum di-generate (null) dan list dosenPengampu tidak null
        assertNull(mk.getId());
        assertNotNull(mk.getDosenPengampu());
        assertTrue(mk.getDosenPengampu().isEmpty());
    }

    // Test parameterized constructor dan getter-nya
    @Test
    public void testParameterizedConstructor() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma dan Struktur Data", "Deskripsi Mata Kuliah", 3);
        assertEquals("IF1234", mk.getKode());
        assertEquals("Algoritma dan Struktur Data", mk.getNama());
        assertEquals("Deskripsi Mata Kuliah", mk.getDeskripsi());
        assertEquals(3, mk.getSks());
        assertNotNull(mk.getDosenPengampu());
        assertTrue(mk.getDosenPengampu().isEmpty());
    }

    // Test setter untuk properti kode, nama, deskripsi, dan sks
    @Test
    public void testSetters() {
        MataKuliah mk = new MataKuliah();
        mk.setKode("IF5678");
        mk.setNama("Pemrograman Lanjut");
        mk.setDeskripsi("Pengenalan ke pemrograman tingkat lanjut");
        mk.setSks(4);

        assertEquals("IF5678", mk.getKode());
        assertEquals("Pemrograman Lanjut", mk.getNama());
        assertEquals("Pengenalan ke pemrograman tingkat lanjut", mk.getDeskripsi());
        assertEquals(4, mk.getSks());
    }

    // Test validasi pada setter sks (nilai negatif harus melempar exception)
    @Test
    public void testSetSksNegativeThrowsException() {
        MataKuliah mk = new MataKuliah();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            mk.setSks(-1);
        });
        assertEquals("SKS tidak boleh negatif", exception.getMessage());
    }

    // Test penambahan dosenPengampu pada relasi many-to-many
    @Test
    public void testDosenPengampu() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma", "Deskripsi", 3);

        // Membuat dummy User untuk dosen
        User dosenDummy = new User();
        dosenDummy.setId(1L);
        dosenDummy.setUsername("dosen1");

        // Tambahkan dummy user ke list dosenPengampu
        mk.getDosenPengampu().add(dosenDummy);
        assertEquals(1, mk.getDosenPengampu().size());
        assertEquals("dosen1", mk.getDosenPengampu().get(0).getUsername());
    }
}
