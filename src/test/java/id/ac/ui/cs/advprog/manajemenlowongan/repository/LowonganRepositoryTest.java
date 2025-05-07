package id.ac.ui.cs.advprog.manajemenlowongan.repository;

import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LowonganRepositoryTest {
    private LowonganRepository repository;

    @BeforeEach
    void setUp() {
        repository = new LowonganRepository();
        repository.getLowongan().clear();
    }

    @Test
    void testGetLowongan() {
        Lowongan lowongan = new Lowongan.Builder().build();
        repository.addLowongan(lowongan);

        List<Lowongan> lowonganList = repository.getLowongan();
        assertEquals(1, lowonganList.size());
        assertTrue(lowonganList.contains(lowongan));
    }

    @Test
    void testGetLowonganById() {
        Lowongan lowongan = new Lowongan.Builder().build();
        repository.addLowongan(lowongan);

        Lowongan fetchedLowongan = repository.getLowonganById(lowongan.getId());
        assertNotNull(fetchedLowongan);
        assertEquals(lowongan.getId(), fetchedLowongan.getId());
    }

    @Test
    void testAddLowongan() {
        Lowongan lowongan = new Lowongan.Builder().build();
        Lowongan addedLowongan = repository.addLowongan(lowongan);

        assertNotNull(addedLowongan);
        assertEquals(lowongan, addedLowongan);
    }

    @Test
    void testUpdateLowongan() {
        Lowongan lowongan = new Lowongan.Builder().build();
        repository.addLowongan(lowongan);

        Lowongan updatedLowongan = new Lowongan.Builder().totalAsdosNeeded(5).totalAsdosRegistered(300).totalAsdosAccepted(5).build();

        Lowongan result = repository.updateLowongan(lowongan.getId(), updatedLowongan);
        assertNotNull(result);
        assertEquals(5, result.getTotalAsdosNeeded());
        assertEquals(300, result.getTotalAsdosRegistered());
        assertEquals(5, result.getTotalAsdosAccepted());
    }

    @Test
    void testDeleteLowongan() {
        Lowongan lowongan = new Lowongan.Builder().build();
        repository.addLowongan(lowongan);
        repository.deleteLowongan(lowongan.getId());
        assertNull(repository.getLowonganById(lowongan.getId()));
    }
}