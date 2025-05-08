package id.ac.ui.cs.advprog.mendaftarlowongan.repository;

import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LamaranRepositoryTest {

    @Autowired
    private LamaranRepository repository;

    @Test
    void testCreateAndFindAll() {
        Lamaran lamaran = new Lamaran.Builder()
                .lowongan(UUID.randomUUID())
                .mahasiswa(UUID.randomUUID())
                .ipk(3.5f)
                .sks(120)
                .status(StatusLamaran.MENUNGGU)
                .build();

        repository.save(lamaran);

        assertEquals(1, repository.findAll().size());
        assertTrue(repository.findAll().contains(lamaran));
    }


    @Test
    void testFindById() {
        Lamaran lamaran = new Lamaran.Builder().build();
        repository.save(lamaran);

        Optional<Lamaran> found = repository.findById(lamaran.getId());
        assertTrue(found.isPresent());
        assertEquals(lamaran.getId(), found.get().getId());
    }

    @Test
    void testUpdateLamaran() {
        Lamaran lamaran = new Lamaran.Builder().build();
        repository.save(lamaran);

        lamaran.setIpk(3.8f);
        lamaran.setSks(150);
        lamaran.setStatus(StatusLamaran.DITERIMA);
        repository.save(lamaran);  // update

        Lamaran updated = repository.findById(lamaran.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(150, updated.getSks());
        assertEquals(3.8f, updated.getIpk());
        assertEquals(StatusLamaran.DITERIMA, updated.getStatus());
    }

    @Test
    void testDeleteLamaran() {
        Lamaran lamaran = new Lamaran.Builder().build();
        repository.save(lamaran);

        repository.deleteById(lamaran.getId());

        assertFalse(repository.findById(lamaran.getId()).isPresent());
    }
}
