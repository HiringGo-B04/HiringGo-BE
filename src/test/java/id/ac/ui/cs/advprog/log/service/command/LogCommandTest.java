package id.ac.ui.cs.advprog.log.service.command;

import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.model.Log;
import id.ac.ui.cs.advprog.log.model.LogBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class LogCommandTest {

    private Log log;

    @BeforeEach
    void setUp() {
        log = new LogBuilder()
                .judul("Asistensi 1")
                .keterangan("Membantu Asistensi PBP")
                .kategori(KategoriLog.ASISTENSI)
                .tanggalLog(LocalDate.now())
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .status(StatusLog.MENUNGGU)
                .build();
    }

    @Test
    void testApproveLogCommandShouldSetStatusToDiterima() {
        LogCommand approveCommand = new ApproveLogCommand(log);
        approveCommand.execute();
        assertEquals(StatusLog.DITERIMA, log.getStatus());
    }

    @Test
    void testRejectLogCommandShouldSetStatusToDitolak() {
        LogCommand rejectCommand = new RejectLogCommand(log);
        rejectCommand.execute();
        assertEquals(StatusLog.DITOLAK, log.getStatus());
    }
}
