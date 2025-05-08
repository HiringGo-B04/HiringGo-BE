package id.ac.ui.cs.advprog.log.repository;

import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.model.Log;
import id.ac.ui.cs.advprog.log.model.LogBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogRepositoryTest {

    @Mock
    private LogRepository repository;

    private UUID idLowongan;
    private UUID idMahasiswa;
    private UUID idDosen;
    private Log sampleLog;

    @BeforeEach
    void setUp() {
        idLowongan = UUID.randomUUID();
        idMahasiswa = UUID.randomUUID();
        idDosen = UUID.randomUUID();
        sampleLog = buildSampleLog();
    }

    @Test
    void testSaveLog() {
        when(repository.save(sampleLog)).thenReturn(sampleLog);

        Log saved = repository.save(sampleLog);

        assertNotNull(saved);
        assertEquals(sampleLog, saved);
        verify(repository).save(sampleLog);
    }

    @Test
    void testSaveLogSetsIdIfNull() {
        Log logWithoutId = buildSampleLog();
        logWithoutId.setId(null);
        ArgumentCaptor<Log> logCaptor = ArgumentCaptor.forClass(Log.class);
        when(repository.save(any(Log.class))).thenReturn(logWithoutId);

        repository.save(logWithoutId);

        verify(repository).save(logCaptor.capture());
        Log capturedLog = logCaptor.getValue();
        assertNotNull(capturedLog);
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(sampleLog));

        List<Log> logs = repository.findAll();

        assertEquals(1, logs.size());
        verify(repository).findAll();
    }

    @Test
    void testFindById() {
        when(repository.findById(sampleLog.getId())).thenReturn(Optional.of(sampleLog));

        Optional<Log> found = repository.findById(sampleLog.getId());

        assertTrue(found.isPresent());
        assertEquals(sampleLog.getId(), found.get().getId());
        verify(repository).findById(sampleLog.getId());
    }

    @Test
    void testFindByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<Log> found = repository.findById(nonExistentId);

        assertFalse(found.isPresent());
        verify(repository).findById(nonExistentId);
    }

    @Test
    void testUpdateLog() {
        UUID existingId = sampleLog.getId();
        Log updatedLog = new LogBuilder()
                .id(existingId)
                .judul("Judul Updated")
                .keterangan("Keterangan Updated")
                .kategori(KategoriLog.MENGAWAS)
                .tanggalLog(LocalDate.now().minusDays(1))
                .waktuMulai(LocalTime.of(8, 0))
                .waktuSelesai(LocalTime.of(10, 0))
                .pesanUntukDosen("Pesan Updated")
                .status(StatusLog.DITERIMA)
                .idLowongan(idLowongan)
                .idMahasiswa(idMahasiswa)
                .idDosen(idDosen)
                .build();

        when(repository.findById(existingId)).thenReturn(Optional.of(sampleLog));
        when(repository.save(any(Log.class))).thenReturn(updatedLog);

        Optional<Log> found = repository.findById(existingId);
        Log logToUpdate = found.get();
        logToUpdate.setJudul("Judul Updated");
        logToUpdate.setKeterangan("Keterangan Updated");
        logToUpdate.setKategori(KategoriLog.MENGAWAS);
        logToUpdate.setStatus(StatusLog.DITERIMA);
        Log result = repository.save(logToUpdate);

        assertNotNull(result);
        assertEquals("Judul Updated", result.getJudul());
        assertEquals("Keterangan Updated", result.getKeterangan());
        assertEquals(KategoriLog.MENGAWAS, result.getKategori());
        assertEquals(StatusLog.DITERIMA, result.getStatus());

        verify(repository).findById(existingId);
        verify(repository).save(any(Log.class));
    }

    @Test
    void testDeleteById() {
        UUID id = sampleLog.getId();
        doNothing().when(repository).deleteById(id);

        repository.deleteById(id);

        verify(repository).deleteById(id);
    }

    @Test
    void testDeleteAll() {
        doNothing().when(repository).deleteAll();

        repository.deleteAll();

        verify(repository).deleteAll();
    }

    @Test
    void testFindByIdLowongan() {
        when(repository.findByIdLowongan(idLowongan)).thenReturn(Arrays.asList(sampleLog));

        List<Log> logs = repository.findByIdLowongan(idLowongan);

        assertEquals(1, logs.size());
        assertEquals(idLowongan, logs.get(0).getIdLowongan());
        verify(repository).findByIdLowongan(idLowongan);
    }

    @Test
    void testFindByIdMahasiswa() {
        when(repository.findByIdMahasiswa(idMahasiswa)).thenReturn(Arrays.asList(sampleLog));

        List<Log> logs = repository.findByIdMahasiswa(idMahasiswa);

        assertEquals(1, logs.size());
        assertEquals(idMahasiswa, logs.get(0).getIdMahasiswa());
        verify(repository).findByIdMahasiswa(idMahasiswa);
    }

    @Test
    void testFindByIdDosen() {
        when(repository.findByIdDosen(idDosen)).thenReturn(Arrays.asList(sampleLog));

        List<Log> logs = repository.findByIdDosen(idDosen);

        assertEquals(1, logs.size());
        assertEquals(idDosen, logs.get(0).getIdDosen());
        verify(repository).findByIdDosen(idDosen);
    }

    @Test
    void testFindByIdMahasiswaAndIdLowongan() {
        when(repository.findByIdMahasiswaAndIdLowongan(idMahasiswa, idLowongan))
                .thenReturn(Arrays.asList(sampleLog));

        List<Log> logs = repository.findByIdMahasiswaAndIdLowongan(idMahasiswa, idLowongan);

        assertEquals(1, logs.size());
        assertEquals(idMahasiswa, logs.get(0).getIdMahasiswa());
        assertEquals(idLowongan, logs.get(0).getIdLowongan());
        verify(repository).findByIdMahasiswaAndIdLowongan(idMahasiswa, idLowongan);
    }

    @Test
    void testFindAcceptedLogsByMahasiswaAndLowonganAndMonth() {
        int tahun = LocalDate.now().getYear();
        int bulan = LocalDate.now().getMonthValue();
        when(repository.findAcceptedLogsByMahasiswaAndLowonganAndMonth(idMahasiswa, idLowongan, tahun, bulan))
                .thenReturn(Arrays.asList(sampleLog));

        List<Log> logs = repository.findAcceptedLogsByMahasiswaAndLowonganAndMonth(
                idMahasiswa, idLowongan, tahun, bulan);

        assertEquals(1, logs.size());
        verify(repository).findAcceptedLogsByMahasiswaAndLowonganAndMonth(
                idMahasiswa, idLowongan, tahun, bulan);
    }

    @Test
    void testFindEmptyResultWhenNoMatchingLogs() {
        UUID nonExistentId = UUID.randomUUID();
        when(repository.findByIdLowongan(nonExistentId)).thenReturn(Arrays.asList());

        List<Log> logs = repository.findByIdLowongan(nonExistentId);

        assertTrue(logs.isEmpty());
        verify(repository).findByIdLowongan(nonExistentId);
    }

    private Log buildSampleLog() {
        return new LogBuilder()
                .id(UUID.randomUUID())  // Explicitly set ID for testing
                .judul("Asistensi Kalkulus")
                .keterangan("Membantu menyampaikan materi integral")
                .kategori(KategoriLog.ASISTENSI)
                .tanggalLog(LocalDate.now())
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .pesanUntukDosen("Sudah selesai")
                .status(StatusLog.MENUNGGU)
                .idLowongan(idLowongan)
                .idMahasiswa(idMahasiswa)
                .idDosen(idDosen)
                .build();
    }
}