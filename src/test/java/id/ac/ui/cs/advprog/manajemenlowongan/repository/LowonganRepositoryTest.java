package id.ac.ui.cs.advprog.manajemenlowongan.repository;

import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
public class LowonganRepositoryTest {

    @Mock
    private LowonganRepository lowonganRepository;

    private UUID lowonganId;
    private Lowongan lowongan1;
    private Lowongan lowongan2;

    @BeforeEach
    void setUp() {
        lowonganId = UUID.randomUUID();

        lowongan1 = new Lowongan.Builder()
                .matkul("Pemrograman Lanjut")
                .year(2024)
                .term("Genap")
                .totalAsdosNeeded(5)
                .build();

        lowongan2 = new Lowongan.Builder()
                .matkul("Struktur Data")
                .year(2024)
                .term("Ganjil")
                .totalAsdosNeeded(3)
                .build();
    }

    @Test
    void testFindById() {
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowongan1));

        Optional<Lowongan> result = lowonganRepository.findById(lowonganId);
        assertTrue(result.isPresent());
        assertEquals("Pemrograman Lanjut", result.get().getMatkul());
    }

    @Test
    void testFindAll() {
        when(lowonganRepository.findAll()).thenReturn(Arrays.asList(lowongan1, lowongan2));

        List<Lowongan> result = lowonganRepository.findAll();
        assertEquals(2, result.size());
        assertEquals("Struktur Data", result.get(1).getMatkul());
    }

    @Test
    void testSaveLowongan() {
        when(lowonganRepository.save(any(Lowongan.class))).thenReturn(lowongan1);

        Lowongan saved = lowonganRepository.save(lowongan1);
        assertNotNull(saved);
        assertEquals("Pemrograman Lanjut", saved.getMatkul());
    }
}