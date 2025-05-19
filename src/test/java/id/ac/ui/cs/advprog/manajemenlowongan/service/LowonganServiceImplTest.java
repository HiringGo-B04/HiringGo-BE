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
                .tahun(2025)
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

//    @Test
//    void testAddLowonganInvalidYearThrowsException() {
//        // Asumsikan ada validasi pada tahun (contoh: tahun tidak boleh kurang dari 2020)
//        Lowongan invalidLowongan = new Lowongan.Builder()
//                .matkul("Adpro")
//                .year(2000) // Invalid year
//                .term("Genap")
//                .totalAsdosNeeded(10)
//                .totalAsdosRegistered(0)
//                .totalAsdosAccepted(0)
//                .build();
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            lowonganService.addLowongan(invalidLowongan);
//        });
//
//        verify(lowonganRepository, never()).addLowongan(any());
//    }

    @Test
    void testGetAllLowongan() {
        List<Lowongan> list = List.of(dummyLowongan);
        when(lowonganRepository.getLowongan()).thenReturn(list);

        List<Lowongan> result = lowonganService.getLowongan();

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
        when(lowonganRepository.updateLowongan(eq(dummyLowongan.getId()), any(Lowongan.class))).thenAnswer(i -> i.getArgument(1));

        dummyLowongan.setTotalAsdosNeeded(15);
        Lowongan updated = lowonganService.updateLowongan(dummyLowongan.getId(), dummyLowongan);

        assertEquals(15, updated.getTotalAsdosNeeded());
        verify(lowonganRepository).updateLowongan(dummyLowongan.getId(), dummyLowongan);
    }

    @Test
    void testDeleteLowongan() {
        UUID id = dummyLowongan.getId();
        lowonganService.deleteLowongan(id);
        verify(lowonganRepository, times(1)).deleteLowongan(id);
    }

//    @Test
//    void testIsLowonganExistsTrue() {
//        when(lowonganRepository.getLowongan()).thenReturn(List.of(dummyLowongan));
//
//        boolean exists = lowonganService.isLowonganExists(dummyLowongan);
//
//        assertTrue(exists);
//    }

    @Test
    void testIsLowonganExistsFalse() {
        when(lowonganRepository.getLowongan()).thenReturn(List.of());

        boolean exists = lowonganService.isLowonganExists(dummyLowongan);

        assertFalse(exists);
    }
}