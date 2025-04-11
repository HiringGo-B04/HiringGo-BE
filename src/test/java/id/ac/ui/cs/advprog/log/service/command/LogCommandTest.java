package id.ac.ui.cs.advprog.log.service.command;

import id.ac.ui.cs.advprog.log.model.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class LogCommandTest {

    private Log log;

    @BeforeEach
    void setUp() {
        log = new Log();
        log.setJudul("Asistensi 1");
        log.setKeterangan("Membantu praktikum STI");
        log.setKategori("Asistensi");
        log.setTanggalLog(LocalDate.now());
        log.setWaktuMulai(LocalTime.of(10, 0));
        log.setWaktuSelesai(LocalTime.of(12, 0));
        log.setStatus("MENUNGGU");
    }

    @Test
    void testApproveLogCommandShouldSetStatusToDiterima() {
        LogCommand approveCommand = new ApproveLogCommand(log);
        approveCommand.execute();

        assertEquals("DITERIMA", log.getStatus());
    }

    @Test
    void testRejectLogCommandShouldSetStatusToDitolak() {
        LogCommand rejectCommand = new RejectLogCommand(log);
        rejectCommand.execute();

        assertEquals("DITOLAK", log.getStatus());
    }
}
