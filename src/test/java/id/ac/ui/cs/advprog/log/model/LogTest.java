package id.ac.ui.cs.advprog.log.model;

import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LogTest {

    private Log log;
    private UUID idLowongan;
    private UUID idMahasiswa;
    private UUID idDosen;

    @BeforeEach
    void setUp() {
        idLowongan = UUID.randomUUID();
        idMahasiswa = UUID.randomUUID();
        idDosen = UUID.randomUUID();

        log = new Log();
        log.setId(UUID.randomUUID());
        log.setJudul("Asistensi");
        log.setKeterangan("Membantu asistensi");
        log.setKategori(KategoriLog.ASISTENSI);
        log.setTanggalLog(LocalDate.now());
        log.setWaktuMulai(LocalTime.of(10, 0));
        log.setWaktuSelesai(LocalTime.of(12, 0));
        log.setPesanUntukDosen("Pesan");
        log.setStatus(StatusLog.MENUNGGU);
        log.setIdLowongan(idLowongan);
        log.setIdMahasiswa(idMahasiswa);
        log.setIdDosen(idDosen);
    }

    @Test
    void testSetAndGetJudul() {
        log.setJudul("Asistensi Baru");
        assertEquals("Asistensi Baru", log.getJudul());
    }

    @Test
    void testSetAndGetStatus() {
        log.setStatus(StatusLog.DITERIMA);
        assertEquals(StatusLog.DITERIMA, log.getStatus());
    }

    @Test
    void testSetAndGetKategori() {
        log.setKategori(KategoriLog.MENGAWAS);
        assertEquals(KategoriLog.MENGAWAS, log.getKategori());
    }

    @Test
    void testSetAndGetIdLowongan() {
        UUID newId = UUID.randomUUID();
        log.setIdLowongan(newId);
        assertEquals(newId, log.getIdLowongan());
    }

    @Test
    void testSetAndGetIdMahasiswa() {
        UUID newId = UUID.randomUUID();
        log.setIdMahasiswa(newId);
        assertEquals(newId, log.getIdMahasiswa());
    }

    @Test
    void testSetAndGetIdDosen() {
        UUID newId = UUID.randomUUID();
        log.setIdDosen(newId);
        assertEquals(newId, log.getIdDosen());
    }
}