package id.ac.ui.cs.advprog.course.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class MataKuliahTest {

    // Test default constructor: pastikan field dosenPengampu diinisialisasi dan field lain default (null atau 0)
    @Test
    public void testDefaultConstructor() {
        MataKuliah mk = new MataKuliah();
        assertNotNull(mk.getDosenPengampu(), "dosenPengampu harus tidak null");
        assertTrue(mk.getDosenPengampu().isEmpty(), "List dosenPengampu harus kosong");
        assertNull(mk.getKode(), "Kode harus null pada konstruktor kosong");
        assertNull(mk.getNama());
        assertNull(mk.getDeskripsi());
        assertEquals(0, mk.getSks());
    }

    // Test parameterized constructor
    @Test
    public void testParameterizedConstructor() {
        MataKuliah mk = new MataKuliah("IF1234", "Algoritma", "Deskripsi Mata Kuliah", 3);
        assertEquals("IF1234", mk.getKode());
        assertEquals("Algoritma", mk.getNama());
        assertEquals("Deskripsi Mata Kuliah", mk.getDeskripsi());
        assertEquals(3, mk.getSks());
        assertNotNull(mk.getDosenPengampu());
        assertTrue(mk.getDosenPengampu().isEmpty(), "dosenPengampu harus kosong saat inisialisasi");
    }

    // Test setter dan getter untuk properti dasar
    @Test
    public void testSettersAndGetters() {
        MataKuliah mk = new MataKuliah();
        mk.setKode("IF5678");
        mk.setNama("Pemrograman Lanjut");
        mk.setDeskripsi("Deskripsi Pemrograman Lanjut");
        mk.setSks(4);

        assertEquals("IF5678", mk.getKode());
        assertEquals("Pemrograman Lanjut", mk.getNama());
        assertEquals("Deskripsi Pemrograman Lanjut", mk.getDeskripsi());
        assertEquals(4, mk.getSks());
    }

    // Test penambahan dosen melalui helper method addDosenPengampu
    @Test
    public void testAddDosenPengampu() {
        MataKuliah mk = new MataKuliah();
        mk.addDosenPengampu("Dosen A");
        assertEquals(1, mk.getDosenPengampu().size());
        assertEquals("Dosen A", mk.getDosenPengampu().get(0));
    }

    // Test setter langsung pada field dosenPengampu
    @Test
    public void testSetDosenPengampu() {
        MataKuliah mk = new MataKuliah();
        List<String> dosenList = new ArrayList<>();
        dosenList.add("Dosen B");
        dosenList.add("Dosen C");
        mk.setDosenPengampu(dosenList);

        assertEquals(2, mk.getDosenPengampu().size());
        assertEquals("Dosen B", mk.getDosenPengampu().get(0));
        assertEquals("Dosen C", mk.getDosenPengampu().get(1));
    }
}
