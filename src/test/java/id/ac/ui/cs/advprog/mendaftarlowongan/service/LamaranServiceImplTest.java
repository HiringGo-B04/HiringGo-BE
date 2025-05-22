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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        CompletableFuture<Lamaran> completableResult = lamaranService.createLamaran(dummyLamaranDTO, dummyLamaranDTO.getIdMahasiswa());

        Lamaran result = completableResult.join();

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
            try {
                lamaranService.createLamaran(dummyLamaranDTO, dummyLamaranDTO.getIdMahasiswa()).get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e.getCause());
            }
        });

        assertTrue(ex.getMessage().contains("IPK tidak valid"));
    }



    @Test
    void testGetLamaran() {
        List<Lamaran> list = List.of(dummyLamaran);
        when(lamaranRepository.findAll()).thenReturn(list);

        CompletableFuture<List<Lamaran>> completableResult = lamaranService.getLamaran();
        List<Lamaran> result = completableResult.join();

        assertEquals(1, result.size());
        assertEquals(dummyLamaran, result.getFirst());
    }

    @Test
    void testGetLamaranByIdFound() {
        when(lamaranRepository.findById(dummyLamaran.getId())).thenReturn(Optional.ofNullable(dummyLamaran));

        CompletableFuture<Lamaran> found = lamaranService.getLamaranById(dummyLamaran.getId());
        Lamaran result = found.join();

        assertNotNull(result);
        assertEquals(dummyLamaran, result);
    }

    @Test
    void testGetLamaranByIdNotFound() {
        when(lamaranRepository.findById(any())).thenReturn(Optional.empty());

        CompletableFuture<Lamaran> completableResult = lamaranService.getLamaranById(UUID.randomUUID());
        Lamaran result = completableResult.join();

        assertNull(result);
    }

    @Test
    void testUpdateLamaran() {
        when(lamaranRepository.findById(dummyLamaran.getId())).thenReturn(Optional.ofNullable(dummyLamaran));
        when(lamaranRepository.save(any(Lamaran.class))).thenAnswer(i -> i.getArgument(0));

        dummyLamaran.setIpk(3.8f);
        CompletableFuture<Lamaran> completableUpdated = lamaranService.updateLamaran(dummyLamaran.getId(), dummyLamaran);
        Lamaran updated = completableUpdated.join();

        assertEquals(3.8f, updated.getIpk());
        verify(lamaranRepository).save(updated);
    }

    @Test
    void testDeleteLamaran() throws ExecutionException, InterruptedException {
        UUID id = dummyLamaran.getId();
        lamaranService.deleteLamaran(id).get();
        verify(lamaranRepository, times(1)).deleteById(id);
    }

    @Test
    void testIsLamaranExistsTrue() {
        when(lamaranRepository.findAll()).thenReturn(List.of(dummyLamaran));

        CompletableFuture<Boolean> complatableExists = lamaranService.isLamaranExists(dummyLamaran);
        boolean exists = complatableExists.join();

        assertTrue(exists);
    }

    @Test
    void testIsLamaranExistsFalse() {
        when(lamaranRepository.findAll()).thenReturn(List.of());

        CompletableFuture<Boolean> completableExists = lamaranService.isLamaranExists(dummyLamaran);
        Boolean exists = completableExists.join();

        assertFalse(exists);
    }

    @Test
    void testGetLamaranByLowonganId() {
        UUID idLowongan = dummyLamaranDTO.getIdLowongan();
        when(lamaranRepository.findAll()).thenReturn(List.of(dummyLamaran));

        CompletableFuture<List<Lamaran>> completableResult = lamaranService.getLamaranByLowonganId(idLowongan);
        List<Lamaran> result = completableResult.join();

        assertEquals(1, result.size());
        assertEquals(idLowongan, result.get(0).getIdLowongan());
    }

    @Test
    void testAcceptLamaran() throws Exception {
        when(lamaranRepository.findById(dummyLamaran.getId())).thenReturn(Optional.ofNullable(dummyLamaran));
        lamaranService.acceptLamaran(dummyLamaran.getId()).get();

        assertEquals(StatusLamaran.DITERIMA, dummyLamaran.getStatus());
        verify(lamaranRepository).save(dummyLamaran);
    }

    @Test
    void testRejectLamaran() throws Exception {
        when(lamaranRepository.findById(dummyLamaran.getId())).thenReturn(Optional.ofNullable(dummyLamaran));
        lamaranService.rejectLamaran(dummyLamaran.getId()).get();

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
