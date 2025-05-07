package id.ac.ui.cs.advprog.log.repository;
import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.model.Log;
import id.ac.ui.cs.advprog.log.model.LogBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogRepositoryTest {

    private LogRepository repository;

    @BeforeEach
    void setUp() {
        repository = new LogRepository();
        repository.clearAll(); // reset data
    }

    @Test
    void testFindAll() {
        Log log = buildSampleLog();
        repository.save(log);

        List<Log> logs = repository.findAll();
        assertEquals(1, logs.size());
        assertTrue(logs.contains(log));
    }

    @Test
    void testFindById() {
        Log log = buildSampleLog();
        repository.save(log);

        Log found = repository.findById(log.getId());
        assertNotNull(found);
        assertEquals(log.getId(), found.getId());
    }

    @Test
    void testSaveLog() {
        Log log = buildSampleLog();
        Log saved = repository.save(log);

        assertNotNull(saved);
        assertEquals(log, saved);
    }

    @Test
    void testUpdateLog() {
        Log log = buildSampleLog();
        repository.save(log);

        Log updatedLog = new LogBuilder()
                .judul("Update Judul")
                .keterangan("Update Keterangan")
                .kategori(KategoriLog.MENGOREKSI)
                .tanggalLog(LocalDate.now())
                .waktuMulai(LocalTime.of(8, 0))
                .waktuSelesai(LocalTime.of(10, 0))
                .pesanUntukDosen("Update Pesan")
                .status(StatusLog.DITERIMA)
                .build();
        updatedLog.setId(log.getId());

        Log result = repository.update(log.getId(), updatedLog);

        assertNotNull(result);
        assertEquals("Update Judul", result.getJudul());
        assertEquals(StatusLog.DITERIMA, result.getStatus());
    }

    @Test
    void testDeleteLog() {
        Log log = buildSampleLog();
        repository.save(log);

        repository.deleteById(log.getId());
        Log deleted = repository.findById(log.getId());

        assertNull(deleted);
        assertEquals(0, repository.findAll().size());
    }

    private Log buildSampleLog() {
        return new LogBuilder()
                .judul("Asistensi Kalkulus")
                .keterangan("Membantu menyampaikan materi integral")
                .kategori(KategoriLog.ASISTENSI)
                .tanggalLog(LocalDate.now())
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .pesanUntukDosen("Sudah selesai")
                .status(StatusLog.MENUNGGU)
                .build();
    }
}
