package id.ac.ui.cs.advprog.mendaftarlowongan.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.mendaftarlowongan.dto.LamaranDTO;
import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.repository.LamaranRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LamaranServiceImplTest {

    private LamaranRepository lamaranRepository;
    private LamaranServiceImpl lamaranService;
    private LowonganRepository lowonganRepository;
    private UserRepository userRepository;

    private LamaranDTO dummyLamaranDTO;
    private Lamaran dummyLamaran;

    @BeforeEach
    void setUp() {
        lamaranRepository = mock(LamaranRepository.class);
        lowonganRepository = mock(LowonganRepository.class);
        userRepository = mock(UserRepository.class);
        lamaranService = new LamaranServiceImpl(lamaranRepository, lowonganRepository, userRepository);

        dummyLamaranDTO = new LamaranDTO(
                20,
                3.5f,
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        dummyLamaran = lamaranService.toEntity(dummyLamaranDTO);
    }

    @Test
    void testCreateLamaranSuccess() {
        // Mock behavior
        when(userRepository.findById(dummyLamaranDTO.getIdMahasiswa()))
                .thenReturn(Optional.of(new User()));
        when(lowonganRepository.findById(dummyLamaranDTO.getIdLowongan()))
                .thenReturn(Optional.of(new Lowongan()));
        when(lamaranRepository.save(any(Lamaran.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Lamaran result = lamaranService.createLamaran(dummyLamaranDTO);

        // Assert
        assertNotNull(result);
        assertEquals(dummyLamaranDTO.getIpk(), result.getIpk());
        assertEquals(dummyLamaranDTO.getSks(), result.getSks());
        verify(lamaranRepository).save(any(Lamaran.class));
    }


    @Test
    void testCreateLamaranInvalidIpkThrowsException() {
        dummyLamaranDTO.setIpk(5.0f); // IPK tidak valid (>4)

        when(userRepository.findById(dummyLamaranDTO.getIdMahasiswa()))
                .thenReturn(Optional.of(new User()));
        when(lowonganRepository.findById(dummyLamaranDTO.getIdLowongan()))
                .thenReturn(Optional.of(new Lowongan()));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            lamaranService.createLamaran(dummyLamaranDTO);
        });

        assertTrue(ex.getMessage().contains("IPK tidak valid"));
    }



    @Test
    void testGetLamaran() {
        List<Lamaran> list = List.of(dummyLamaran);
        when(lamaranRepository.findAll()).thenReturn(list);

        List<Lamaran> result = lamaranService.getLamaran();

        assertEquals(1, result.size());
        assertEquals(dummyLamaran, result.getFirst());
    }

    @Test
    void testGetLamaranByIdFound() {
        when(lamaranRepository.findById(dummyLamaran.getId())).thenReturn(Optional.ofNullable(dummyLamaran));

        Lamaran found = lamaranService.getLamaranById(dummyLamaran.getId());

        assertNotNull(found);
        assertEquals(dummyLamaran, found);
    }

    @Test
    void testGetLamaranByIdNotFound() {
        when(lamaranRepository.findById(any())).thenReturn(Optional.empty());

        Lamaran result = lamaranService.getLamaranById(UUID.randomUUID());

        assertNull(result);
    }

    @Test
    void testUpdateLamaran() {
        when(lamaranRepository.findById(dummyLamaran.getId())).thenReturn(Optional.ofNullable(dummyLamaran));
        when(lamaranRepository.save(any(Lamaran.class))).thenAnswer(i -> i.getArgument(0));

        dummyLamaran.setIpk(3.8f);
        Lamaran updated = lamaranService.updateLamaran(dummyLamaran.getId(), dummyLamaran);

        assertEquals(3.8f, updated.getIpk());
        verify(lamaranRepository).save(updated);
    }

    @Test
    void testDeleteLamaran() {
        UUID id = dummyLamaran.getId();
        lamaranService.deleteLamaran(id);
        verify(lamaranRepository, times(1)).deleteById(id);
    }

    @Test
    void testIsLamaranExistsTrue() {
        when(lamaranRepository.findAll()).thenReturn(List.of(dummyLamaran));

        boolean exists = lamaranService.isLamaranExists(dummyLamaran);

        assertTrue(exists);
    }

    @Test
    void testIsLamaranExistsFalse() {
        when(lamaranRepository.findAll()).thenReturn(List.of());

        boolean exists = lamaranService.isLamaranExists(dummyLamaran);

        assertFalse(exists);
    }

    @Test
    void testGetLamaranByLowonganId() {
        UUID idLowongan = dummyLamaranDTO.getIdLowongan();
        when(lamaranRepository.findAll()).thenReturn(List.of(dummyLamaran));

        List<Lamaran> result = lamaranService.getLamaranByLowonganId(idLowongan);

        assertEquals(1, result.size());
        assertEquals(idLowongan, result.get(0).getIdLowongan());
    }

    @Test
    void testAcceptLamaran() {
        when(lamaranRepository.findById(dummyLamaran.getId())).thenReturn(Optional.ofNullable(dummyLamaran));
        lamaranService.acceptLamaran(dummyLamaran.getId());

        assertEquals(StatusLamaran.DITERIMA, dummyLamaran.getStatus());
        verify(lamaranRepository).save(dummyLamaran);
    }

    @Test
    void testRejectLamaran() {
        when(lamaranRepository.findById(dummyLamaran.getId())).thenReturn(Optional.ofNullable(dummyLamaran));
        lamaranService.rejectLamaran(dummyLamaran.getId());

        assertEquals(StatusLamaran.DITOLAK, dummyLamaran.getStatus());
        verify(lamaranRepository).save(dummyLamaran);
    }

    @Test
    void testToEntity() {
        Lamaran lamaran = lamaranService.toEntity(dummyLamaranDTO);

        assertNotNull(lamaran);
        assertEquals(dummyLamaranDTO.getIpk(), lamaran.getIpk());
        assertEquals(dummyLamaranDTO.getSks(), lamaran.getSks());
        assertEquals(dummyLamaranDTO.getIdMahasiswa(), lamaran.getIdMahasiswa());
        assertEquals(dummyLamaranDTO.getIdLowongan(), lamaran.getIdLowongan());
    }
}
