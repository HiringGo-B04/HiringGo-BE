package id.ac.ui.cs.advprog.mendaftarlowongan.repository;

import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LamaranRepositoryTest {
    private LamaranRepository repository;

    @BeforeEach
    void setUp() {
        repository = LamaranRepository.getInstance();
        repository.getLamaran().clear();
    }

    @Test
    void testGetLamaran() {
        Lamaran lamaran = new Lamaran.Builder().build();
        repository.createLamaran(lamaran);

        List<Lamaran> lamarans = repository.getLamaran();
        assertEquals(1, lamarans.size());
        assertTrue(lamarans.contains(lamaran));
    }

    @Test
    void testGetLamaranById() {
        Lamaran lamaran = new Lamaran.Builder().build();
        repository.createLamaran(lamaran);

        Lamaran fetchedLamaran = repository.getLamaranById(lamaran.getId());
        assertNotNull(fetchedLamaran);
        assertEquals(lamaran.getId(), fetchedLamaran.getId());
    }

    @Test
    void testAddLamaran() {
        Lamaran lamaran = new Lamaran.Builder().build();
        Lamaran addedLamaran = repository.createLamaran(lamaran);

        assertNotNull(addedLamaran);
        assertEquals(lamaran, addedLamaran);
    }

    @Test
    void testUpdateLamaran() {
        Lamaran lamaran = new Lamaran.Builder().build();
        repository.createLamaran(lamaran);

        Lamaran updatedLamaran = new Lamaran.Builder().sks(150).ipk(3.8f).status(StatusLamaran.DITERIMA).build();

        Lamaran result = repository.updateLamaran(lamaran.getId(), updatedLamaran);
        assertNotNull(result);
        assertEquals(150, result.getSks());
        assertEquals(3.8f, result.getIpk());
        assertEquals(StatusLamaran.DITERIMA, result.getStatus());
    }

    @Test
    void testDeleteLamaran() {
        Lamaran lamaran = new Lamaran.Builder().build();
        repository.createLamaran(lamaran);
        repository.deleteLamaran(lamaran.getId());
        assertNull(repository.getLamaranById(lamaran.getId()));
    }
}