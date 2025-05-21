package id.ac.ui.cs.advprog.log.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.log.dto.LogDTO;
import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.exception.BadRequestException;
import id.ac.ui.cs.advprog.log.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.log.model.Log;
import id.ac.ui.cs.advprog.log.model.LogBuilder;
import id.ac.ui.cs.advprog.log.repository.LogRepository;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.repository.LamaranRepository;

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
        when(logRepository.findById(sampleLog.getId())).thenReturn(Optional.of(sampleLog));
        when(logRepository.save(any(Log.class))).thenReturn(sampleLog);

        Log updated = buildLog();
        updated.setJudul("Update Judul");

        Log result = logService.update(sampleLog.getId(), updated);

        assertEquals("Update Judul", sampleLog.getJudul());
        verify(logRepository).findById(sampleLog.getId());
        verify(logRepository).save(sampleLog);
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
        when(userRepository.findById(idMahasiswa)).thenReturn(Optional.of(mahasiswa));
        when(lowonganRepository.findById(idLowongan)).thenReturn(Optional.ofNullable(lowongan));
        when(logRepository.findByIdMahasiswaAndIdLowongan(idMahasiswa, idLowongan))
                .thenReturn(Collections.singletonList(sampleLog));

        List<Log> result = logService.findByMahasiswaAndLowongan(idMahasiswa, idLowongan);

        assertEquals(1, result.size());
        verify(userRepository).findById(idMahasiswa);
        verify(lowonganRepository).findById(idLowongan);
        verify(logRepository).findByIdMahasiswaAndIdLowongan(idMahasiswa, idLowongan);
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

        when(userRepository.findById(idMahasiswa)).thenReturn(Optional.of(mahasiswa));
        when(userRepository.findById(idDosen)).thenReturn(Optional.of(dosen));
        when(lowonganRepository.findById(idLowongan)).thenReturn(Optional.ofNullable(lowongan));
        when(lamaranRepository.findAll()).thenReturn(Collections.singletonList(lamaran));
        when(logRepository.save(any(Log.class))).thenReturn(sampleLog);

        Log result = logService.createLogForMahasiswa(logDTO, idMahasiswa, idDosen);

        assertNotNull(result);
        verify(userRepository).findById(idMahasiswa);
        verify(userRepository).findById(idDosen);
        verify(lowonganRepository).findById(idLowongan);
        verify(lamaranRepository).findAll();
        verify(logRepository).save(any(Log.class));
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

        assertThrows(BadRequestException.class, () ->
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
        sampleLog.setIdDosen(idDosen);

        when(logRepository.findById(sampleLog.getId())).thenReturn(Optional.of(sampleLog));
        when(logRepository.save(sampleLog)).thenReturn(sampleLog);

        Log result = logService.verifyLog(sampleLog.getId(), StatusLog.DITERIMA, idDosen);

        assertEquals(StatusLog.DITERIMA, result.getStatus());
        verify(logRepository).findById(sampleLog.getId());
        verify(logRepository).save(sampleLog);
    }

    @Test
    void testVerifyLog_NotOwner() {
        UUID differentDosenId = UUID.randomUUID();
        sampleLog.setIdDosen(idDosen);

        when(logRepository.findById(sampleLog.getId())).thenReturn(Optional.of(sampleLog));

        assertThrows(BadRequestException.class, () ->
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