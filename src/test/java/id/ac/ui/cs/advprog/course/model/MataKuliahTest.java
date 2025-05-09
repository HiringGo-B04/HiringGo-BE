package id.ac.ui.cs.advprog.course.model;

import id.ac.ui.cs.advprog.authjwt.model.User;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MataKuliahTest {

    /* ---------- Helper ---------- */
    private User dummyLecturer(String name) {
        User u = new User(UUID.randomUUID(), name + "@mail.com", "secret");
        u.setFullName(name);
        return u;
    }

    /* ---------- Default constructor ---------- */
    @Test
    void ctor_default_shouldInitialiseEmptySet() {
        MataKuliah mk = new MataKuliah();

        assertNotNull(mk.getDosenPengampu(), "dosenPengampu harus di‑inisialisasi");
        assertTrue(mk.getDosenPengampu().isEmpty(), "dosenPengampu harus kosong");
        assertNull(mk.getKode());
        assertNull(mk.getNama());
        assertNull(mk.getDeskripsi());
        assertEquals(0, mk.getSks());
    }

    /* ---------- All‑args constructor ---------- */
    @Test
    void ctor_allArgs_shouldSetAllFields() {
        Set<User> lecturers = new HashSet<>();
        MataKuliah mk = new MataKuliah(
                "IF1234",
                "Algoritma",
                "Mempelajari algoritma dasar",
                3,
                lecturers
        );

        assertEquals("IF1234", mk.getKode());
        assertEquals("Algoritma", mk.getNama());
        assertEquals("Mempelajari algoritma dasar", mk.getDeskripsi());
        assertEquals(3, mk.getSks());
        assertSame(lecturers, mk.getDosenPengampu());
    }

    /* ---------- Setter & Getter ---------- */
    @Test
    void settersAndGetters_shouldPersistValues() {
        MataKuliah mk = new MataKuliah();
        mk.setKode("IF5678");
        mk.setNama("Pemrograman Lanjut");
        mk.setDeskripsi("Java generics & concurrency");
        mk.setSks(4);

        assertAll(
                () -> assertEquals("IF5678", mk.getKode()),
                () -> assertEquals("Pemrograman Lanjut", mk.getNama()),
                () -> assertEquals("Java generics & concurrency", mk.getDeskripsi()),
                () -> assertEquals(4, mk.getSks())
        );
    }

    /* ---------- addDosenPengampu helper ---------- */
    @Test
    void addDosenPengampu_shouldAddUniqueLecturer() {
        MataKuliah mk = new MataKuliah();
        User dosenA = dummyLecturer("dosenA");

        mk.addDosenPengampu(dosenA);

        assertEquals(1, mk.getDosenPengampu().size());
        assertTrue(mk.getDosenPengampu().contains(dosenA));
    }

    /* ---------- setDosenPengampu ---------- */
    @Test
    void setDosenPengampu_shouldReplaceWholeCollection() {
        MataKuliah mk = new MataKuliah();

        Set<User> newSet = Set.of(dummyLecturer("dosenB"), dummyLecturer("dosenC"));
        mk.setDosenPengampu(new HashSet<>(newSet));

        assertEquals(2, mk.getDosenPengampu().size());
        assertTrue(mk.getDosenPengampu().containsAll(newSet));
    }
}
