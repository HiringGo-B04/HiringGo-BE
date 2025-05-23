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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LamaranServiceImplTest {

    private LamaranRepository lamaranRepository;
    private LamaranServiceImpl lamaranService;
    private LowonganRepository lowonganRepository;
    private UserRepository userRepository;
    private ThreadPoolTaskExecutor taskExecutor;

    private LamaranDTO dummyLamaranDTO;
    private Lamaran dummyLamaran;

    @BeforeEach
    void setUp() {
        lamaranRepository = mock(LamaranRepository.class);
        lowonganRepository = mock(LowonganRepository.class);
        userRepository = mock(UserRepository.class);

        // Create and configure task executor similar to AsyncConfig
        taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.setQueueCapacity(10);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix("Test-");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(10);
        taskExecutor.initialize();

        // Create service instance and inject dependencies
        lamaranService = new LamaranServiceImpl(lamaranRepository, lowonganRepository, userRepository);

        // Use reflection to set the executor field since it's private
        try {
            var executorField = LamaranServiceImpl.class.getDeclaredField("executor");
            executorField.setAccessible(true);
            executorField.set(lamaranService, taskExecutor);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject executor", e);
        }

        dummyLamaranDTO = new LamaranDTO(
                20,
                3.5f,
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        dummyLamaran = lamaranService.toEntity(dummyLamaranDTO);
    }

    @AfterEach
    void tearDown() {
        if (taskExecutor != null) {
            taskExecutor.shutdown();
            try {
                if (!taskExecutor.getThreadPoolExecutor().awaitTermination(5, TimeUnit.SECONDS)) {
                    taskExecutor.getThreadPoolExecutor().shutdownNow();
                }
            } catch (InterruptedException e) {
                taskExecutor.getThreadPoolExecutor().shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test
    void testCreateLamaranSuccess() throws ExecutionException, InterruptedException {
        // Mock behavior
        when(userRepository.findById(dummyLamaranDTO.getIdMahasiswa()))
                .thenReturn(Optional.of(new User()));
        when(lowonganRepository.findById(dummyLamaranDTO.getIdLowongan()))
                .thenReturn(Optional.of(new Lowongan()));
        when(lamaranRepository.findAll()).thenReturn(Collections.emptyList()); // No existing applications
        when(lamaranRepository.save(any(Lamaran.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CompletableFuture<Lamaran> completableResult = lamaranService.createLamaran(dummyLamaranDTO, dummyLamaranDTO.getIdMahasiswa());
        Lamaran result = completableResult.get(); // Use get() for proper exception handling

        // Assert
        assertNotNull(result);
        assertEquals(dummyLamaranDTO.getIpk(), result.getIpk());
        assertEquals(dummyLamaranDTO.getSks(), result.getSks());
        assertEquals(StatusLamaran.MENUNGGU, result.getStatus());
        verify(lamaranRepository).save(any(Lamaran.class));
    }

    @Test
    void testCreateLamaranInvalidUserIdThrowsException() {
        UUID differentUserId = UUID.randomUUID();

        // Act & Assert
        ExecutionException ex = assertThrows(ExecutionException.class, () -> {
            lamaranService.createLamaran(dummyLamaranDTO, differentUserId).get();
        });

        assertTrue(ex.getCause().getMessage().contains("ID Mahasiswa tidak sesuai dengan userId pada token"));
    }

    @Test
    void testCreateLamaranInvalidIpkThrowsException() {
        dummyLamaranDTO.setIpk(5.0f); // IPK tidak valid (>4)

        when(lamaranRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        ExecutionException ex = assertThrows(ExecutionException.class, () -> {
            lamaranService.createLamaran(dummyLamaranDTO, dummyLamaranDTO.getIdMahasiswa()).get();
        });

        assertTrue(ex.getCause().getMessage().contains("IPK tidak valid"));
    }

    @Test
    void testCreateLamaranInvalidSksThrowsException() {
        dummyLamaranDTO.setSks(25); // SKS tidak valid (>24)

        when(lamaranRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        ExecutionException ex = assertThrows(ExecutionException.class, () -> {
            lamaranService.createLamaran(dummyLamaranDTO, dummyLamaranDTO.getIdMahasiswa()).get();
        });

        assertTrue(ex.getCause().getMessage().contains("SKS tidak valid"));
    }

    @Test
    void testCreateLamaranAlreadyExistsThrowsException() {
        when(lamaranRepository.findAll()).thenReturn(List.of(dummyLamaran)); // Existing application

        // Act & Assert
        ExecutionException ex = assertThrows(ExecutionException.class, () -> {
            lamaranService.createLamaran(dummyLamaranDTO, dummyLamaranDTO.getIdMahasiswa()).get();
        });

        assertTrue(ex.getCause().getMessage().contains("Sudah pernah melamar"));
    }

    @Test
    void testGetLamaran() throws ExecutionException, InterruptedException {
        List<Lamaran> list = List.of(dummyLamaran);
        when(lamaranRepository.findAll()).thenReturn(list);

        CompletableFuture<List<Lamaran>> completableResult = lamaranService.getLamaran();
        List<Lamaran> result = completableResult.get();

        assertEquals(1, result.size());
        assertEquals(dummyLamaran, result.get(0));
        verify(lamaranRepository).findAll();
    }

    @Test
    void testGetLamaranByIdFound() throws ExecutionException, InterruptedException {
        when(lamaranRepository.findById(dummyLamaran.getId())).thenReturn(Optional.of(dummyLamaran));

        CompletableFuture<Lamaran> found = lamaranService.getLamaranById(dummyLamaran.getId());
        Lamaran result = found.get();

        assertNotNull(result);
        assertEquals(dummyLamaran, result);
        verify(lamaranRepository).findById(dummyLamaran.getId());
    }

    @Test
    void testGetLamaranByIdNotFound() throws ExecutionException, InterruptedException {
        UUID randomId = UUID.randomUUID();
        when(lamaranRepository.findById(randomId)).thenReturn(Optional.empty());

        CompletableFuture<Lamaran> completableResult = lamaranService.getLamaranById(randomId);
        Lamaran result = completableResult.get();

        assertNull(result);
        verify(lamaranRepository).findById(randomId);
    }

    @Test
    void testUpdateLamaran() throws ExecutionException, InterruptedException {
        when(lamaranRepository.findById(dummyLamaran.getId())).thenReturn(Optional.of(dummyLamaran));
        when(lamaranRepository.save(any(Lamaran.class))).thenAnswer(i -> i.getArgument(0));

        Lamaran updatedData = new Lamaran();
        updatedData.setIpk(3.8f);
        updatedData.setSks(22);
        updatedData.setStatus(StatusLamaran.DITERIMA);
        updatedData.setIdMahasiswa(dummyLamaran.getIdMahasiswa());
        updatedData.setIdLowongan(dummyLamaran.getIdLowongan());

        CompletableFuture<Lamaran> completableUpdated = lamaranService.updateLamaran(dummyLamaran.getId(), updatedData);
        Lamaran updated = completableUpdated.get();

        assertNotNull(updated);
        assertEquals(3.8f, updated.getIpk());
        assertEquals(22, updated.getSks());
        assertEquals(StatusLamaran.DITERIMA, updated.getStatus());
        verify(lamaranRepository).save(any(Lamaran.class));
    }

    @Test
    void testUpdateLamaranNotFound() throws ExecutionException, InterruptedException {
        UUID randomId = UUID.randomUUID();
        when(lamaranRepository.findById(randomId)).thenReturn(Optional.empty());

        Lamaran updatedData = new Lamaran();
        CompletableFuture<Lamaran> completableUpdated = lamaranService.updateLamaran(randomId, updatedData);
        Lamaran result = completableUpdated.get();

        assertNull(result);
        verify(lamaranRepository, never()).save(any(Lamaran.class));
    }

    @Test
    void testDeleteLamaran() throws ExecutionException, InterruptedException {
        UUID id = dummyLamaran.getId();

        CompletableFuture<Void> completableResult = lamaranService.deleteLamaran(id);
        completableResult.get(); // Wait for completion

        verify(lamaranRepository, times(1)).deleteById(id);
    }

    @Test
    void testIsLamaranExistsTrue() throws ExecutionException, InterruptedException {
        when(lamaranRepository.findAll()).thenReturn(List.of(dummyLamaran));

        CompletableFuture<Boolean> completableExists = lamaranService.isLamaranExists(dummyLamaran);
        boolean exists = completableExists.get();

        assertTrue(exists);
        verify(lamaranRepository).findAll();
    }

    @Test
    void testIsLamaranExistsFalse() throws ExecutionException, InterruptedException {
        when(lamaranRepository.findAll()).thenReturn(Collections.emptyList());

        CompletableFuture<Boolean> completableExists = lamaranService.isLamaranExists(dummyLamaran);
        Boolean exists = completableExists.get();

        assertFalse(exists);
        verify(lamaranRepository).findAll();
    }

    @Test
    void testValidateLamaranValid() throws ExecutionException, InterruptedException {
        when(lamaranRepository.findAll()).thenReturn(Collections.emptyList());

        CompletableFuture<Void> completableResult = lamaranService.validateLamaran(dummyLamaran);

        // Should complete without exception
        assertDoesNotThrow(() -> completableResult.get());
        verify(lamaranRepository).findAll();
    }

    @Test
    void testGetLamaranByLowonganId() throws ExecutionException, InterruptedException {
        UUID idLowongan = dummyLamaranDTO.getIdLowongan();
        when(lamaranRepository.findAll()).thenReturn(List.of(dummyLamaran));

        CompletableFuture<List<Lamaran>> completableResult = lamaranService.getLamaranByLowonganId(idLowongan);
        List<Lamaran> result = completableResult.get();

        assertEquals(1, result.size());
        assertEquals(idLowongan, result.get(0).getIdLowongan());
        verify(lamaranRepository).findAll();
    }

    @Test
    void testGetLamaranByLowonganIdEmpty() throws ExecutionException, InterruptedException {
        UUID idLowongan = UUID.randomUUID();
        when(lamaranRepository.findAll()).thenReturn(List.of(dummyLamaran));

        CompletableFuture<List<Lamaran>> completableResult = lamaranService.getLamaranByLowonganId(idLowongan);
        List<Lamaran> result = completableResult.get();

        assertEquals(0, result.size());
        verify(lamaranRepository).findAll();
    }

    @Test
    void testAcceptLamaran() throws ExecutionException, InterruptedException {
        when(lamaranRepository.findById(dummyLamaran.getId())).thenReturn(Optional.of(dummyLamaran));
        when(lamaranRepository.save(any(Lamaran.class))).thenAnswer(i -> i.getArgument(0));

        CompletableFuture<Void> completableResult = lamaranService.acceptLamaran(dummyLamaran.getId());
        completableResult.get();

        assertEquals(StatusLamaran.DITERIMA, dummyLamaran.getStatus());
        verify(lamaranRepository).save(dummyLamaran);
    }

    @Test
    void testAcceptLamaranNotFound() throws ExecutionException, InterruptedException {
        UUID randomId = UUID.randomUUID();
        when(lamaranRepository.findById(randomId)).thenReturn(Optional.empty());

        CompletableFuture<Void> completableResult = lamaranService.acceptLamaran(randomId);

        // Should complete without exception even if lamaran not found
        assertDoesNotThrow(() -> completableResult.get());
        verify(lamaranRepository, never()).save(any(Lamaran.class));
    }

    @Test
    void testRejectLamaran() throws ExecutionException, InterruptedException {
        when(lamaranRepository.findById(dummyLamaran.getId())).thenReturn(Optional.of(dummyLamaran));
        when(lamaranRepository.save(any(Lamaran.class))).thenAnswer(i -> i.getArgument(0));

        CompletableFuture<Void> completableResult = lamaranService.rejectLamaran(dummyLamaran.getId());
        completableResult.get();

        assertEquals(StatusLamaran.DITOLAK, dummyLamaran.getStatus());
        verify(lamaranRepository).save(dummyLamaran);
    }

    @Test
    void testRejectLamaranNotFound() throws ExecutionException, InterruptedException {
        UUID randomId = UUID.randomUUID();
        when(lamaranRepository.findById(randomId)).thenReturn(Optional.empty());

        CompletableFuture<Void> completableResult = lamaranService.rejectLamaran(randomId);

        // Should complete without exception even if lamaran not found
        assertDoesNotThrow(() -> completableResult.get());
        verify(lamaranRepository, never()).save(any(Lamaran.class));
    }

    @Test
    void testToEntity() {
        Lamaran lamaran = lamaranService.toEntity(dummyLamaranDTO);

        assertNotNull(lamaran);
        assertEquals(dummyLamaranDTO.getIpk(), lamaran.getIpk());
        assertEquals(dummyLamaranDTO.getSks(), lamaran.getSks());
        assertEquals(dummyLamaranDTO.getIdMahasiswa(), lamaran.getIdMahasiswa());
        assertEquals(dummyLamaranDTO.getIdLowongan(), lamaran.getIdLowongan());
        assertEquals(StatusLamaran.MENUNGGU, lamaran.getStatus());
    }
}