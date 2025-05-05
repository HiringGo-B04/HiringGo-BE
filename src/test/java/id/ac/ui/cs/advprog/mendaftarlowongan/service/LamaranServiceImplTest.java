package id.ac.ui.cs.advprog.mendaftarlowongan.service;

import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.repository.LamaranRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LamaranServiceImplTest {

    private LamaranRepository lamaranRepository;
    private LamaranServiceImpl lamaranService;

    private Lamaran dummyLamaran;

    @BeforeEach
    void setUp() {
        lamaranRepository = mock(LamaranRepository.class);
        lamaranService = new LamaranServiceImpl(lamaranRepository);

        dummyLamaran = new Lamaran.Builder()
                .sks(20)
                .ipk(3.5f)
                .status(StatusLamaran.MENUNGGU)
                .mahasiswa(UUID.randomUUID())
                .lowongan(UUID.randomUUID())
                .build();
    }

    @Test
    void testCreateLamaranSuccess() {
        when(lamaranRepository.createLamaran(any(Lamaran.class))).thenReturn(dummyLamaran);

        Lamaran created = lamaranService.createLamaran(dummyLamaran);

        assertEquals(dummyLamaran, created);
        verify(lamaranRepository, times(1)).createLamaran(dummyLamaran);
    }

    @Test
    void testCreateLamaranInvalidIpkThrowsException() {
        dummyLamaran.setIpk(5.0f);  // Invalid

        assertThrows(IllegalArgumentException.class, () -> {
            lamaranService.createLamaran(dummyLamaran);
        });

        verify(lamaranRepository, never()).createLamaran(any());
    }

    @Test
    void testGetLamaran() {
        List<Lamaran> list = List.of(dummyLamaran);
        when(lamaranRepository.getLamaran()).thenReturn(list);

        List<Lamaran> result = lamaranService.getLamaran();

        assertEquals(1, result.size());
        assertEquals(dummyLamaran, result.get(0));
    }

    @Test
    void testGetLamaranByIdFound() {
        when(lamaranRepository.getLamaranById(dummyLamaran.getId())).thenReturn(dummyLamaran);

        Lamaran found = lamaranService.getLamaranById(dummyLamaran.getId());

        assertNotNull(found);
        assertEquals(dummyLamaran, found);
    }

    @Test
    void testGetLamaranByIdNotFound() {
        when(lamaranRepository.getLamaranById(any())).thenReturn(null);

        Lamaran result = lamaranService.getLamaranById(UUID.randomUUID());

        assertNull(result);
    }

    @Test
    void testUpdateLamaran() {
        when(lamaranRepository.getLamaranById(dummyLamaran.getId())).thenReturn(dummyLamaran);
        when(lamaranRepository.createLamaran(any(Lamaran.class))).thenAnswer(i -> i.getArgument(0));

        dummyLamaran.setIpk(3.8f);
        Lamaran updated = lamaranService.updateLamaran(dummyLamaran.getId(), dummyLamaran);

        assertEquals(3.8f, updated.getIpk());
        verify(lamaranRepository).createLamaran(updated);
    }

    @Test
    void testDeleteLamaran() {
        UUID id = dummyLamaran.getId();
        lamaranService.deleteLamaran(id);
        verify(lamaranRepository, times(1)).deleteLamaran(id);
    }

    @Test
    void testIsLamaranExistsTrue() {
        when(lamaranRepository.getLamaran()).thenReturn(List.of(dummyLamaran));

        boolean exists = lamaranService.isLamaranExists(dummyLamaran);

        assertTrue(exists);
    }

    @Test
    void testIsLamaranExistsFalse() {
        when(lamaranRepository.getLamaran()).thenReturn(List.of());

        boolean exists = lamaranService.isLamaranExists(dummyLamaran);

        assertFalse(exists);
    }

    @Test
    void testGetLamaranByLowonganId() {
        UUID idLowongan = dummyLamaran.getIdLowongan();
        when(lamaranRepository.getLamaran()).thenReturn(List.of(dummyLamaran));

        List<Lamaran> result = lamaranService.getLamaranByLowonganId(idLowongan);

        assertEquals(1, result.size());
        assertEquals(idLowongan, result.get(0).getIdLowongan());
    }

    @Test
    void testAcceptLamaran() {
        when(lamaranRepository.getLamaranById(dummyLamaran.getId())).thenReturn(dummyLamaran);
        lamaranService.acceptLamaran(dummyLamaran.getId());

        assertEquals(StatusLamaran.DITERIMA, dummyLamaran.getStatus());
        verify(lamaranRepository).createLamaran(dummyLamaran);
    }

    @Test
    void testRejectLamaran() {
        when(lamaranRepository.getLamaranById(dummyLamaran.getId())).thenReturn(dummyLamaran);
        lamaranService.rejectLamaran(dummyLamaran.getId());

        assertEquals(StatusLamaran.DITOLAK, dummyLamaran.getStatus());
        verify(lamaranRepository).createLamaran(dummyLamaran);
    }
}
