package id.ac.ui.cs.advprog.log.service;
import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.model.Log;
import id.ac.ui.cs.advprog.log.model.LogBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class LogServiceImplTest {

    private LogService logService;

    @BeforeEach
    void setUp() {
        logService = new LogServiceImpl();
        ((LogServiceImpl) logService).findAll().clear();
    }

    @Test
    void testCreateLog() {
        Log log = buildLog();
        Log saved = logService.create(log);

        assertNotNull(saved);
        assertEquals(KategoriLog.ASISTENSI, saved.getKategori());
    }

    @Test
    void testFindById() {
        Log log = buildLog();
        logService.create(log);

        Log found = logService.findById(log.getId());
        assertEquals(log.getJudul(), found.getJudul());
    }

    @Test
    void testUpdateLog() {
        Log log = buildLog();
        logService.create(log);

        Log updated = buildLog();
        updated.setJudul("Update Judul");

        Log result = logService.update(log.getId(), updated);
        assertEquals("Update Judul", result.getJudul());
    }

    @Test
    void testDeleteLog() {
        Log log = buildLog();
        logService.create(log);

        logService.delete(log.getId());
        assertNull(logService.findById(log.getId()));
    }

    private Log buildLog() {
        return new LogBuilder()
                .judul("Asistensi")
                .keterangan("Membantu kelas")
                .kategori(KategoriLog.ASISTENSI)
                .tanggalLog(LocalDate.now())
                .waktuMulai(LocalTime.of(9, 0))
                .waktuSelesai(LocalTime.of(11, 0))
                .status(StatusLog.MENUNGGU)
                .build();
    }
}
