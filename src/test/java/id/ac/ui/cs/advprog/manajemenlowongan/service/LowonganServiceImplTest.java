package id.ac.ui.cs.advprog.manajemenlowongan.service;

import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LowonganServiceImplTest {

    private LowonganRepository lowonganRepository;
    private LowonganServiceImpl lowonganService;

    private Lowongan dummyLowongan;

    @BeforeEach
    void setUp() {
        lowonganRepository = mock(LowonganRepository.class);
        lowonganService = new LowonganServiceImpl(lowonganRepository);

        dummyLowongan = new Lowongan.Builder()
                .matkul("Adpro")
                .year(2025)
                .term("Genap")
                .totalAsdosNeeded(10)
                .totalAsdosRegistered(0)
                .totalAsdosAccepted(0)
                .build();
    }

    @Test
    void testAddLowonganSuccess() {
        when(lowonganRepository.addLowongan(any(Lowongan.class))).thenReturn(dummyLowongan);

        Lowongan created = lowonganService.addLowongan(dummyLowongan);

        assertEquals(dummyLowongan, created);
        verify(lowonganRepository, times(1)).addLowongan(dummyLowongan);
    }

    @Test
    void testAddLowonganInvalidYearThrowsException() {
        // Asumsikan ada validasi pada tahun (contoh: tahun tidak boleh kurang dari 2020)
        Lowongan invalidLowongan = new Lowongan.Builder()
                .matkul("Adpro")
                .year(2000) // Invalid year
                .term("Genap")
                .totalAsdosNeeded(10)
                .totalAsdosRegistered(0)
                .totalAsdosAccepted(0)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.addLowongan(invalidLowongan);
        });

        verify(lowonganRepository, never()).addLowongan(any());
    }

    @Test
    void testGetAllLowongan() {
        List<Lowongan> list = List.of(dummyLowongan);
        when(lowonganRepository.getAllLowongan()).thenReturn(list);

        List<Lowongan> result = lowonganService.getAllLowongan();

        assertEquals(1, result.size());
        assertEquals(dummyLowongan, result.get(0));
    }

    @Test
    void testGetLowonganByIdFound() {
        when(lowonganRepository.getLowonganById(dummyLowongan.getId())).thenReturn(dummyLowongan);

        Lowongan found = lowonganService.getLowonganById(dummyLowongan.getId());

        assertNotNull(found);
        assertEquals(dummyLowongan, found);
    }

    @Test
    void testGetLowonganByIdNotFound() {
        when(lowonganRepository.getLowonganById(any())).thenReturn(null);

        Lowongan result = lowonganService.getLowonganById(UUID.randomUUID());

        assertNull(result);
    }

    @Test
    void testUpdateLowongan() {
        when(lowonganRepository.getLowonganById(dummyLowongan.getId())).thenReturn(dummyLowongan);
        when(lowonganRepository.updateLowongan(any(Lowongan.class))).thenAnswer(i -> i.getArgument(0));

        dummyLowongan.setTotalAsdosNeeded(15);
        Lowongan updated = lowonganService.updateLowongan(dummyLowongan.getId(), dummyLowongan);

        assertEquals(15, updated.getTotalAsdosNeeded());
        verify(lowonganRepository).updateLowongan(updated);
    }

    @Test
    void testDeleteLowongan() {
        UUID id = dummyLowongan.getId();
        lowonganService.deleteLowongan(id);
        verify(lowonganRepository, times(1)).deleteLowongan(id);
    }

    @Test
    void testIsLowonganExistsTrue() {
        when(lowonganRepository.getAllLowongan()).thenReturn(List.of(dummyLowongan));

        boolean exists = lowonganService.isLowonganExists(dummyLowongan);

        assertTrue(exists);
    }

    @Test
    void testIsLowonganExistsFalse() {
        when(lowonganRepository.getAllLowongan()).thenReturn(List.of());

        boolean exists = lowonganService.isLowonganExists(dummyLowongan);

        assertFalse(exists);
    }

    @Test
    void testGetLowonganByMatkul() {
        String matkul = dummyLowongan.getMatkul();
        when(lowonganRepository.getAllLowongan()).thenReturn(List.of(dummyLowongan));

        List<Lowongan> result = lowonganService.getLowonganByMatkul(matkul);

        assertEquals(1, result.size());
        assertEquals(matkul, result.get(0).getMatkul());
    }

    @Test
    void testIncrementTotalAsdosRegistered() {
        when(lowonganRepository.getLowonganById(dummyLowongan.getId())).thenReturn(dummyLowongan);
        when(lowonganRepository.updateLowongan(any(Lowongan.class))).thenAnswer(i -> i.getArgument(0));

        lowonganService.incrementTotalAsdosRegistered(dummyLowongan.getId());

        assertEquals(1, dummyLowongan.getTotalAsdosRegistered());
        verify(lowonganRepository).updateLowongan(dummyLowongan);
    }

    @Test
    void testIncrementTotalAsdosAccepted() {
        when(lowonganRepository.getLowonganById(dummyLowongan.getId())).thenReturn(dummyLowongan);
        when(lowonganRepository.updateLowongan(any(Lowongan.class))).thenAnswer(i -> i.getArgument(0));

        lowonganService.incrementTotalAsdosAccepted(dummyLowongan.getId());

        assertEquals(1, dummyLowongan.getTotalAsdosAccepted());
        verify(lowonganRepository).updateLowongan(dummyLowongan);
    }
}