package id.ac.ui.cs.advprog.log.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.log.dto.LogDTO;
import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.exception.BadRequestException;
import id.ac.ui.cs.advprog.log.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.log.exception.ForbiddenException;
import id.ac.ui.cs.advprog.log.model.Log;
import id.ac.ui.cs.advprog.log.model.LogBuilder;
import id.ac.ui.cs.advprog.log.repository.LogRepository;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.repository.LamaranRepository;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogServiceImplTest {

    @Mock
    private LogRepository logRepository;

    @Mock
    private LowonganRepository lowonganRepository;

    @Mock
    private LamaranRepository lamaranRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MataKuliahRepository mataKuliahRepository;

    @InjectMocks
    private LogServiceImpl logService;

    private UUID idLowongan;
    private UUID idMahasiswa;
    private UUID idDosen;
    private Log sampleLog;
    private User mahasiswa;
    private User dosen;
    private Lowongan lowongan;
    private Lamaran lamaran;
    private MataKuliah mataKuliah;

    @BeforeEach
    void setUp() {
        idLowongan = UUID.randomUUID();
        idMahasiswa = UUID.randomUUID();
        idDosen = UUID.randomUUID();

        mahasiswa = new User();
        mahasiswa.setUserId(idMahasiswa);
        mahasiswa.setUsername("mahasiswa@example.com");
        mahasiswa.setRole("STUDENT");

        dosen = new User();
        dosen.setUserId(idDosen);
        dosen.setUsername("dosen@example.com");
        dosen.setRole("LECTURER");

        lowongan = new Lowongan();
        lowongan.setId(idLowongan);
        lowongan.setMatkul("Pemrograman");
        lowongan.setTahun(2024);
        lowongan.setTerm("Ganjil");

        lamaran = new Lamaran();
        lamaran.setId(UUID.randomUUID());
        lamaran.setIdMahasiswa(idMahasiswa);
        lamaran.setIdLowongan(idLowongan);
        lamaran.setStatus(StatusLamaran.DITERIMA);

        mataKuliah = new MataKuliah("CSGE601021", "Pemrograman", "Mata kuliah pemrograman", 3);
        mataKuliah.addDosenPengampu(dosen);

        sampleLog = buildLog();
    }

    @Test
    void testCreateLog() {
        when(logRepository.save(any(Log.class))).thenReturn(sampleLog);

        Log saved = logService.create(sampleLog);

        assertNotNull(saved);
        assertEquals(KategoriLog.ASISTENSI, saved.getKategori());
        verify(logRepository).save(sampleLog);
    }

    @Test
    void testFindAll() {
        List<Log> logs = Collections.singletonList(sampleLog);
        when(logRepository.findAll()).thenReturn(logs);

        List<Log> result = logService.findAll();

        assertEquals(1, result.size());
        verify(logRepository).findAll();
    }

    @Test
    void testFindById_Success() {
        when(logRepository.findById(sampleLog.getId())).thenReturn(Optional.of(sampleLog));

        Log found = logService.findById(sampleLog.getId());

        assertEquals(sampleLog.getJudul(), found.getJudul());
        verify(logRepository).findById(sampleLog.getId());
    }

    @Test
    void testFindById_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(logRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> logService.findById(nonExistentId));
        verify(logRepository).findById(nonExistentId);
    }

    @Test
    void testUpdateLog_Success() {
        Log existingLog = buildLog();
        existingLog.setJudul("Original Judul");

        when(logRepository.findById(existingLog.getId())).thenReturn(Optional.of(existingLog));
        when(logRepository.save(existingLog)).thenReturn(existingLog);

        Log updatedLog = buildLog();
        updatedLog.setJudul("Updated Judul");

        Log result = logService.update(existingLog.getId(), updatedLog);

        assertNotNull(result);
        verify(logRepository).findById(existingLog.getId());
        verify(logRepository).save(existingLog);
    }

    @Test
    void testDeleteLog() {
        when(logRepository.findById(sampleLog.getId())).thenReturn(Optional.of(sampleLog));
        doNothing().when(logRepository).delete(sampleLog);

        logService.delete(sampleLog.getId());

        verify(logRepository).findById(sampleLog.getId());
        verify(logRepository).delete(sampleLog);
    }

    @Test
    void testFindByMahasiswaAndLowongan() {
        // Mock all required validations
        when(userRepository.findById(idMahasiswa)).thenReturn(Optional.of(mahasiswa));
        when(lowonganRepository.findById(idLowongan)).thenReturn(Optional.of(lowongan));

        // Mock the accepted lamaran check
        when(lamaranRepository.findAll()).thenReturn(Collections.singletonList(lamaran));

        when(logRepository.findByIdMahasiswaAndIdLowongan(idMahasiswa, idLowongan))
                .thenReturn(Collections.singletonList(sampleLog));

        List<Log> result = logService.findByMahasiswaAndLowongan(idMahasiswa, idLowongan);

        assertEquals(1, result.size());
        verify(userRepository).findById(idMahasiswa);
        verify(lowonganRepository).findById(idLowongan);
        verify(lamaranRepository).findAll();
        verify(logRepository).findByIdMahasiswaAndIdLowongan(idMahasiswa, idLowongan);
    }

    @Test
    void testFindByIdForMahasiswa_Success() {
        when(logRepository.findById(sampleLog.getId())).thenReturn(Optional.of(sampleLog));
        sampleLog.setIdMahasiswa(idMahasiswa);

        Log result = logService.findByIdForMahasiswa(sampleLog.getId(), idMahasiswa);

        assertNotNull(result);
        assertEquals(sampleLog.getId(), result.getId());
        verify(logRepository).findById(sampleLog.getId());
    }

    @Test
    void testFindByIdForMahasiswa_Forbidden() {
        UUID differentMahasiswaId = UUID.randomUUID();
        when(logRepository.findById(sampleLog.getId())).thenReturn(Optional.of(sampleLog));
        sampleLog.setIdMahasiswa(idMahasiswa);

        assertThrows(ForbiddenException.class, () ->
                logService.findByIdForMahasiswa(sampleLog.getId(), differentMahasiswaId));
    }

    @Test
    void testCreateLogForMahasiswa_Success() {
        LogDTO logDTO = new LogDTO();
        logDTO.setJudul("Asistensi");
        logDTO.setKeterangan("Membantu kelas");
        logDTO.setKategori("ASISTENSI");
        logDTO.setTanggalLog(LocalDate.now());
        logDTO.setWaktuMulai(LocalTime.of(9, 0));
        logDTO.setWaktuSelesai(LocalTime.of(11, 0));
        logDTO.setIdLowongan(idLowongan);

        // Mock the optimized calls - each should be called only once
        when(userRepository.findById(idMahasiswa)).thenReturn(Optional.of(mahasiswa));
        when(lowonganRepository.findById(idLowongan)).thenReturn(Optional.of(lowongan)); // Called once
        when(lamaranRepository.findAll()).thenReturn(Collections.singletonList(lamaran));
        when(mataKuliahRepository.findAll()).thenReturn(Collections.singletonList(mataKuliah)); // Called once
        when(userRepository.findById(idDosen)).thenReturn(Optional.of(dosen)); // For dosen validation
        when(logRepository.save(any(Log.class))).thenReturn(sampleLog);

        Log result = logService.createLogForMahasiswa(logDTO, idMahasiswa);

        // Verify the result
        assertNotNull(result);
        assertEquals("Asistensi", result.getJudul());
        assertEquals("Membantu kelas", result.getKeterangan());
        assertEquals(KategoriLog.ASISTENSI, result.getKategori());
        assertEquals(StatusLog.MENUNGGU, result.getStatus());
        assertEquals(idMahasiswa, result.getIdMahasiswa());
        assertEquals(idDosen, result.getIdDosen());
        assertEquals(idLowongan, result.getIdLowongan());

        // Verify optimized calls - each should be called exactly once
        verify(userRepository, times(1)).findById(idMahasiswa);
        verify(lowonganRepository, times(1)).findById(idLowongan); // OPTIMIZED: Only 1 call
        verify(lamaranRepository, times(1)).findAll();
        verify(mataKuliahRepository, times(1)).findAll(); // OPTIMIZED: Only 1 call
        verify(userRepository, times(1)).findById(idDosen);
        verify(logRepository, times(1)).save(any(Log.class));
    }

    @Test
    void testCreateLogForMahasiswa_MahasiswaNotFound() {
        LogDTO logDTO = new LogDTO();
        logDTO.setIdLowongan(idLowongan);

        when(userRepository.findById(idMahasiswa)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                logService.createLogForMahasiswa(logDTO, idMahasiswa));

        verify(userRepository).findById(idMahasiswa);
        verify(lowonganRepository, never()).findById(any()); // Should not reach lowongan validation
    }

    @Test
    void testCreateLogForMahasiswa_LowonganNotFound() {
        LogDTO logDTO = new LogDTO();
        logDTO.setIdLowongan(idLowongan);

        when(userRepository.findById(idMahasiswa)).thenReturn(Optional.of(mahasiswa));
        when(lowonganRepository.findById(idLowongan)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                logService.createLogForMahasiswa(logDTO, idMahasiswa));

        verify(userRepository).findById(idMahasiswa);
        verify(lowonganRepository).findById(idLowongan);
        verify(lamaranRepository, never()).findAll(); // Should not reach lamaran validation
    }

    @Test
    void testCreateLogForMahasiswa_MahasiswaNotAccepted() {
        LogDTO logDTO = new LogDTO();
        logDTO.setIdLowongan(idLowongan);

        when(userRepository.findById(idMahasiswa)).thenReturn(Optional.of(mahasiswa));
        when(lowonganRepository.findById(idLowongan)).thenReturn(Optional.of(lowongan));
        when(lamaranRepository.findAll()).thenReturn(Collections.emptyList()); // No accepted lamaran

        assertThrows(BadRequestException.class, () ->
                logService.createLogForMahasiswa(logDTO, idMahasiswa));

        verify(userRepository).findById(idMahasiswa);
        verify(lowonganRepository).findById(idLowongan);
        verify(lamaranRepository).findAll();
        verify(mataKuliahRepository, never()).findAll(); // Should not reach mata kuliah lookup
    }

    @Test
    void testCreateLogForMahasiswa_FutureDate() {
        LogDTO logDTO = new LogDTO();
        logDTO.setJudul("Asistensi");
        logDTO.setKeterangan("Membantu kelas");
        logDTO.setKategori("ASISTENSI");
        logDTO.setTanggalLog(LocalDate.now().plusDays(1)); // Future date
        logDTO.setWaktuMulai(LocalTime.of(9, 0));
        logDTO.setWaktuSelesai(LocalTime.of(11, 0));
        logDTO.setIdLowongan(idLowongan);

        when(userRepository.findById(idMahasiswa)).thenReturn(Optional.of(mahasiswa));
        when(lowonganRepository.findById(idLowongan)).thenReturn(Optional.of(lowongan));
        when(lamaranRepository.findAll()).thenReturn(Collections.singletonList(lamaran));

        assertThrows(BadRequestException.class, () ->
                logService.createLogForMahasiswa(logDTO, idMahasiswa));

        verify(userRepository).findById(idMahasiswa);
        verify(lowonganRepository).findById(idLowongan);
        verify(lamaranRepository).findAll();
        verify(mataKuliahRepository, never()).findAll(); // Should not reach mata kuliah lookup
    }

    @Test
    void testCreateLogForMahasiswa_MataKuliahNotFound() {
        LogDTO logDTO = new LogDTO();
        logDTO.setJudul("Asistensi");
        logDTO.setKeterangan("Membantu kelas");
        logDTO.setKategori("ASISTENSI");
        logDTO.setTanggalLog(LocalDate.now());
        logDTO.setWaktuMulai(LocalTime.of(9, 0));
        logDTO.setWaktuSelesai(LocalTime.of(11, 0));
        logDTO.setIdLowongan(idLowongan);

        when(userRepository.findById(idMahasiswa)).thenReturn(Optional.of(mahasiswa));
        when(lowonganRepository.findById(idLowongan)).thenReturn(Optional.of(lowongan));
        when(lamaranRepository.findAll()).thenReturn(Collections.singletonList(lamaran));
        when(mataKuliahRepository.findAll()).thenReturn(Collections.emptyList()); // No mata kuliah found

        assertThrows(ResourceNotFoundException.class, () ->
                logService.createLogForMahasiswa(logDTO, idMahasiswa));

        verify(userRepository).findById(idMahasiswa);
        verify(lowonganRepository).findById(idLowongan);
        verify(lamaranRepository).findAll();
        verify(mataKuliahRepository).findAll();
        verify(userRepository, never()).findById(idDosen); // Should not reach dosen validation
    }

    @Test
    void testCreateLogForMahasiswa_DosenNotFound() {
        LogDTO logDTO = new LogDTO();
        logDTO.setJudul("Asistensi");
        logDTO.setKeterangan("Membantu kelas");
        logDTO.setKategori("ASISTENSI");
        logDTO.setTanggalLog(LocalDate.now());
        logDTO.setWaktuMulai(LocalTime.of(9, 0));
        logDTO.setWaktuSelesai(LocalTime.of(11, 0));
        logDTO.setIdLowongan(idLowongan);

        when(userRepository.findById(idMahasiswa)).thenReturn(Optional.of(mahasiswa));
        when(lowonganRepository.findById(idLowongan)).thenReturn(Optional.of(lowongan));
        when(lamaranRepository.findAll()).thenReturn(Collections.singletonList(lamaran));
        when(mataKuliahRepository.findAll()).thenReturn(Collections.singletonList(mataKuliah));
        when(userRepository.findById(idDosen)).thenReturn(Optional.empty()); // Dosen not found

        assertThrows(ResourceNotFoundException.class, () ->
                logService.createLogForMahasiswa(logDTO, idMahasiswa));

        verify(userRepository).findById(idMahasiswa);
        verify(lowonganRepository).findById(idLowongan);
        verify(lamaranRepository).findAll();
        verify(mataKuliahRepository).findAll();
        verify(userRepository).findById(idDosen);
        verify(logRepository, never()).save(any()); // Should not save
    }

    @Test
    void testUpdateLogForMahasiswa_Success() {
        LogDTO logDTO = new LogDTO();
        logDTO.setJudul("Updated Asistensi");
        logDTO.setKeterangan("Updated Membantu kelas");
        logDTO.setKategori("ASISTENSI");
        logDTO.setTanggalLog(LocalDate.now());
        logDTO.setWaktuMulai(LocalTime.of(9, 0));
        logDTO.setWaktuSelesai(LocalTime.of(11, 0));

        sampleLog.setIdMahasiswa(idMahasiswa);
        sampleLog.setStatus(StatusLog.MENUNGGU);

        when(logRepository.findById(sampleLog.getId())).thenReturn(Optional.of(sampleLog));
        when(logRepository.save(any(Log.class))).thenReturn(sampleLog);

        Log result = logService.updateLogForMahasiswa(sampleLog.getId(), logDTO, idMahasiswa);

        assertNotNull(result);
        verify(logRepository).findById(sampleLog.getId());
        verify(logRepository).save(any(Log.class));
    }

    @Test
    void testUpdateLogForMahasiswa_NotOwner() {
        LogDTO logDTO = new LogDTO();
        UUID differentMahasiswaId = UUID.randomUUID();
        sampleLog.setIdMahasiswa(idMahasiswa);

        when(logRepository.findById(sampleLog.getId())).thenReturn(Optional.of(sampleLog));

        assertThrows(ForbiddenException.class, () ->
                logService.updateLogForMahasiswa(sampleLog.getId(), logDTO, differentMahasiswaId)
        );
    }

    @Test
    void testDeleteLogForMahasiswa_Success() {
        sampleLog.setIdMahasiswa(idMahasiswa);
        sampleLog.setStatus(StatusLog.MENUNGGU);

        when(logRepository.findById(sampleLog.getId())).thenReturn(Optional.of(sampleLog));
        doNothing().when(logRepository).delete(sampleLog);

        logService.deleteLogForMahasiswa(sampleLog.getId(), idMahasiswa);

        verify(logRepository).findById(sampleLog.getId());
        verify(logRepository).delete(sampleLog);
    }

    @Test
    void testVerifyLog_Success() {
        sampleLog.setIdLowongan(idLowongan);
        sampleLog.setStatus(StatusLog.MENUNGGU);

        // Mock the findById call
        when(logRepository.findById(sampleLog.getId())).thenReturn(Optional.of(sampleLog));

        // Mock the ownership validation - dosen validation
        when(userRepository.findById(idDosen)).thenReturn(Optional.of(dosen));

        // Mock the lowongan validation
        when(lowonganRepository.findById(idLowongan)).thenReturn(Optional.of(lowongan));

        // Mock the mata kuliah lookup for ownership check
        when(mataKuliahRepository.findAll()).thenReturn(Collections.singletonList(mataKuliah));

        // Mock the save operation
        when(logRepository.save(sampleLog)).thenReturn(sampleLog);

        Log result = logService.verifyLog(sampleLog.getId(), StatusLog.DITERIMA, idDosen);

        assertEquals(StatusLog.DITERIMA, result.getStatus());
        verify(logRepository).findById(sampleLog.getId());
        verify(userRepository).findById(idDosen);
        verify(lowonganRepository, times(2)).findById(idLowongan);
        verify(mataKuliahRepository).findAll();
        verify(logRepository).save(sampleLog);
    }

    @Test
    void testVerifyLog_NotOwner() {
        UUID differentDosenId = UUID.randomUUID();
        sampleLog.setIdDosen(idDosen);

        when(logRepository.findById(sampleLog.getId())).thenReturn(Optional.of(sampleLog));

        assertThrows(ForbiddenException.class, () ->
                logService.verifyLog(sampleLog.getId(), StatusLog.DITERIMA, differentDosenId)
        );
    }

    @Test
    void testValidateMahasiswa_Valid() {
        when(userRepository.findById(idMahasiswa)).thenReturn(Optional.of(mahasiswa));

        boolean result = logService.validateMahasiswa(idMahasiswa);

        assertTrue(result);
        verify(userRepository).findById(idMahasiswa);
    }

    @Test
    void testValidateMahasiswa_Invalid() {
        when(userRepository.findById(idMahasiswa)).thenReturn(Optional.empty());

        boolean result = logService.validateMahasiswa(idMahasiswa);

        assertFalse(result);
        verify(userRepository).findById(idMahasiswa);
    }

    @Test
    void testValidateDosen_Valid() {
        when(userRepository.findById(idDosen)).thenReturn(Optional.of(dosen));

        boolean result = logService.validateDosen(idDosen);

        assertTrue(result);
        verify(userRepository).findById(idDosen);
    }

    @Test
    void testValidateDosen_Invalid() {
        when(userRepository.findById(idDosen)).thenReturn(Optional.empty());

        boolean result = logService.validateDosen(idDosen);

        assertFalse(result);
        verify(userRepository).findById(idDosen);
    }

    @Test
    void testCalculateHonor() {
        List<Log> acceptedLogs = Arrays.asList(sampleLog);
        when(userRepository.findById(idMahasiswa)).thenReturn(Optional.of(mahasiswa));
        when(lowonganRepository.findById(idLowongan)).thenReturn(Optional.of(lowongan));
        when(logRepository.findAcceptedLogsByMahasiswaAndLowonganAndMonth(
                idMahasiswa, idLowongan, StatusLog.DITERIMA, 2025, 5)).thenReturn(acceptedLogs);

        double result = logService.calculateHonor(idMahasiswa, idLowongan, 2025, 5);

        assertEquals(55000.0, result); // 2 hours * 27500
        verify(logRepository).findAcceptedLogsByMahasiswaAndLowonganAndMonth(
                idMahasiswa, idLowongan, StatusLog.DITERIMA, 2025, 5);
    }

    @Test
    void testCalculateHonorData() {
        List<Log> acceptedLogs = Arrays.asList(sampleLog);
        when(userRepository.findById(idMahasiswa)).thenReturn(Optional.of(mahasiswa));
        when(lowonganRepository.findById(idLowongan)).thenReturn(Optional.of(lowongan));
        when(logRepository.findAcceptedLogsByMahasiswaAndLowonganAndMonth(
                idMahasiswa, idLowongan, StatusLog.DITERIMA, 2025, 5)).thenReturn(acceptedLogs);

        Map<String, Object> result = logService.calculateHonorData(idMahasiswa, idLowongan, 2025, 5);

        assertNotNull(result);
        assertEquals(5, result.get("bulan"));
        assertEquals(2025, result.get("tahun"));
        assertEquals(idLowongan, result.get("lowonganId"));
        assertEquals(55000.0, result.get("honor"));
        assertEquals("Rp 55,000.00", result.get("formattedHonor"));
    }

    private Log buildLog() {
        return new LogBuilder()
                .id(UUID.randomUUID())
                .judul("Asistensi")
                .keterangan("Membantu kelas")
                .kategori(KategoriLog.ASISTENSI)
                .tanggalLog(LocalDate.now())
                .waktuMulai(LocalTime.of(9, 0))
                .waktuSelesai(LocalTime.of(11, 0))
                .status(StatusLog.MENUNGGU)
                .idLowongan(idLowongan)
                .idMahasiswa(idMahasiswa)
                .idDosen(idDosen)
                .build();
    }
}