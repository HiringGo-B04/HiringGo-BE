package id.ac.ui.cs.advprog.course.model;

import id.ac.ui.cs.advprog.authjwt.model.User;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MataKuliahTest {

    private User dummyLecturer(String name) {
        User u = new User();                         // sesuaikan ctor User Anda
        u.setUserId(UUID.randomUUID());
        u.setUsername(name + "@mail.com");
        u.setFullName(name);
        return u;
    }

    @Test
    void ctor_default_shouldInitialiseEmptySet() {
        MataKuliah mk = new MataKuliah();

        assertNotNull(mk.getDosenPengampu(), "dosenPengampu harus di-inisialisasi");
        assertTrue(mk.getDosenPengampu().isEmpty(), "dosenPengampu harus kosong");
        assertNull(mk.getKode());
        assertNull(mk.getNama());
        assertNull(mk.getDeskripsi());
        assertEquals(0, mk.getSks());
    }

    @Test
    void ctor_fourArgs_shouldSetFieldsAndEmptyLecturerSet() {
        MataKuliah mk = new MataKuliah(
                "IF1234",
                "Algoritma",
                "Mempelajari algoritma dasar",
                3
        );

        assertEquals("IF1234", mk.getKode());
        assertEquals("Algoritma", mk.getNama());
        assertEquals("Mempelajari algoritma dasar", mk.getDeskripsi());
        assertEquals(3, mk.getSks());
        assertNotNull(mk.getDosenPengampu());
        assertTrue(mk.getDosenPengampu().isEmpty());
    }

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

    @Test
    void addDosenPengampu_shouldAddUniqueLecturer() {
        MataKuliah mk = new MataKuliah();
        User dosenA = dummyLecturer("dosenA");

        mk.addDosenPengampu(dosenA);

        assertEquals(1, mk.getDosenPengampu().size());
        assertTrue(mk.getDosenPengampu().contains(dosenA));
    }

    @Test
    void addDosenPengampu_whenDosenPengampuIsNull_shouldInitializeAndAdd() {
        MataKuliah mk = new MataKuliah();
        // Force dosenPengampu to be null by setting it explicitly
        mk.setDosenPengampu(null);

        User dosenA = dummyLecturer("dosenA");

        mk.addDosenPengampu(dosenA);

        assertNotNull(mk.getDosenPengampu(), "dosenPengampu should be initialized");
        assertEquals(1, mk.getDosenPengampu().size());
        assertTrue(mk.getDosenPengampu().contains(dosenA));
    }

    @Test
    void removeDosenPengampu_whenDosenPengampuIsNotNull_shouldRemoveLecturer() {
        MataKuliah mk = new MataKuliah();
        User dosenA = dummyLecturer("dosenA");
        User dosenB = dummyLecturer("dosenB");

        // Add both lecturers
        mk.addDosenPengampu(dosenA);
        mk.addDosenPengampu(dosenB);

        assertEquals(2, mk.getDosenPengampu().size());

        // Remove dosenA by userId
        mk.removeDosenPengampu(dosenA.getUserId());

        assertEquals(1, mk.getDosenPengampu().size());
        assertFalse(mk.getDosenPengampu().contains(dosenA));
        assertTrue(mk.getDosenPengampu().contains(dosenB));
    }

    @Test
    void removeDosenPengampu_whenDosenPengampuIsNull_shouldDoNothing() {
        MataKuliah mk = new MataKuliah();
        // Force dosenPengampu to be null
        mk.setDosenPengampu(null);

        UUID randomUserId = UUID.randomUUID();

        // This should not throw an exception and should do nothing
        assertDoesNotThrow(() -> mk.removeDosenPengampu(randomUserId));

        // dosenPengampu should still be null
        assertNull(mk.getDosenPengampu());
    }

    @Test
    void removeDosenPengampu_whenUserIdDoesNotExist_shouldNotRemoveAnything() {
        MataKuliah mk = new MataKuliah();
        User dosenA = dummyLecturer("dosenA");
        User dosenB = dummyLecturer("dosenB");

        mk.addDosenPengampu(dosenA);
        mk.addDosenPengampu(dosenB);

        assertEquals(2, mk.getDosenPengampu().size());

        // Try to remove with a random UUID that doesn't exist
        UUID nonExistentUserId = UUID.randomUUID();
        mk.removeDosenPengampu(nonExistentUserId);

        // Size should remain the same
        assertEquals(2, mk.getDosenPengampu().size());
        assertTrue(mk.getDosenPengampu().contains(dosenA));
        assertTrue(mk.getDosenPengampu().contains(dosenB));
    }

    @Test
    void setDosenPengampu_shouldReplaceWholeCollection() {
        MataKuliah mk = new MataKuliah();

        Set<User> newSet = Set.of(dummyLecturer("dosenB"), dummyLecturer("dosenC"));
        mk.setDosenPengampu(new HashSet<>(newSet));

        assertEquals(2, mk.getDosenPengampu().size());
        assertTrue(mk.getDosenPengampu().containsAll(newSet));
    }

    @Test
    void equals_shouldBeBasedOnKode() {
        MataKuliah mk1 = new MataKuliah("IF1234", "Algoritma", "Desc1", 3);
        MataKuliah mk2 = new MataKuliah("IF1234", "Data Structure", "Desc2", 4);
        MataKuliah mk3 = new MataKuliah("IF5678", "Algoritma", "Desc1", 3);

        assertEquals(mk1, mk2, "MataKuliah with same kode should be equal");
        assertNotEquals(mk1, mk3, "MataKuliah with different kode should not be equal");
        assertEquals(mk1.hashCode(), mk2.hashCode(), "MataKuliah with same kode should have same hashCode");
    }
}