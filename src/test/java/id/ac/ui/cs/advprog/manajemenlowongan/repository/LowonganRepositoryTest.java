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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        UUID id = lowongan1.getId();
        when(lowonganRepository.findById(id)).thenReturn(Optional.of(lowongan1));

        Optional<Lowongan> result = lowonganRepository.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getMatkul()).isEqualTo("Pemrograman Lanjut");
        verify(lowonganRepository, times(1)).findById(id);
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
        when(lowonganRepository.save(lowongan1)).thenReturn(lowongan1);

        Lowongan saved = lowonganRepository.save(lowongan1);
        assertThat(saved).isEqualTo(lowongan1);
        verify(lowonganRepository, times(1)).save(lowongan1);
    }

    @Test
    void testDeleteLowongan() {
        lowonganRepository.delete(lowongan1);
        verify(lowonganRepository, times(1)).delete(lowongan1);
    }

    @Test
    void testUpdateLowongan() {
        Lowongan updated = new Lowongan.Builder()
                .matkul("Pemrograman Lanjut")
                .year(2024)
                .term("Genap")
                .totalAsdosNeeded(10) // Diubah dari 5 ke 10
                .totalAsdosRegistered(3)
                .totalAsdosAccepted(2)
                .build();

        when(lowonganRepository.save(updated)).thenReturn(updated);

        Lowongan result = lowonganRepository.save(updated);
        assertEquals(10, result.getTotalAsdosNeeded());
        assertEquals(3, result.getTotalAsdosRegistered());
    }
}