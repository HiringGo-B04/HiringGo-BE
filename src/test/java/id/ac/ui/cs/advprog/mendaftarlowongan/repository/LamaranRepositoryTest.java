package id.ac.ui.cs.advprog.mendaftarlowongan.repository;

import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LamaranRepositoryTest {

    @Mock
    private LamaranRepository lamaranRepository;

    private UUID lowonganId;
    private Lamaran lamaran1;
    private Lamaran lamaran2;

    @BeforeEach
    void setUp() {
        // Setup test data
        lowonganId = UUID.randomUUID();
        UUID mahasiswaId1 = UUID.randomUUID();
        UUID mahasiswaId2 = UUID.randomUUID();

        lamaran1 = new Lamaran.Builder()
                .sks(23)
                .ipk(3.5f)
                .status(StatusLamaran.MENUNGGU)
                .mahasiswa(mahasiswaId1)
                .lowongan(lowonganId)
                .build();

        lamaran2 = new Lamaran.Builder()
                .sks(22)
                .ipk(3.2f)
                .status(StatusLamaran.DITERIMA)
                .mahasiswa(mahasiswaId2)
                .lowongan(lowonganId)
                .build();
    }

    @Test
    void testSave() {
        // Arrange
        when(lamaranRepository.save(lamaran1)).thenReturn(lamaran1);

        // Act
        Lamaran savedLamaran = lamaranRepository.save(lamaran1);

        // Assert
        assertThat(savedLamaran).isEqualTo(lamaran1);
        verify(lamaranRepository, times(1)).save(lamaran1);
    }

    @Test
    void testFindById() {
        // Arrange
        UUID id = lamaran1.getId();
        when(lamaranRepository.findById(id)).thenReturn(Optional.of(lamaran1));

        // Act
        Optional<Lamaran> foundLamaran = lamaranRepository.findById(id);

        // Assert
        assertThat(foundLamaran).isPresent();
        assertThat(foundLamaran.get()).isEqualTo(lamaran1);
        verify(lamaranRepository, times(1)).findById(id);
    }

    @Test
    void testFindByIdLowongan() {
        // Arrange
        List<Lamaran> expectedLamarans = Arrays.asList(lamaran1, lamaran2);
        when(lamaranRepository.findByIdLowongan(lowonganId)).thenReturn(expectedLamarans);

        // Act
        List<Lamaran> actualLamarans = lamaranRepository.findByIdLowongan(lowonganId);

        // Assert
        assertThat(actualLamarans).hasSize(2);
        assertThat(actualLamarans).containsExactlyInAnyOrderElementsOf(expectedLamarans);
        verify(lamaranRepository, times(1)).findByIdLowongan(lowonganId);
    }

    @Test
    void testDelete() {
        // Act
        lamaranRepository.delete(lamaran1);

        // Assert
        verify(lamaranRepository, times(1)).delete(lamaran1);
    }
}