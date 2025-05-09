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
        log.setKategori(KategoriLog.LAIN_LAIN);
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
    void testSetAndGetKeterangan() {
        log.setKeterangan("Keterangan Baru");
        assertEquals("Keterangan Baru", log.getKeterangan());
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

    @Test
    void testConstructorAndGetters() {
        UUID id = UUID.randomUUID();

        Log logFromBuilder = new LogBuilder()
                .id(id)
                .judul("Test Log")
                .keterangan("Keterangan Test")
                .kategori(KategoriLog.ASISTENSI)
                .tanggalLog(LocalDate.of(2025, 3, 15))
                .waktuMulai(LocalTime.of(8, 0))
                .waktuSelesai(LocalTime.of(10, 0))
                .pesanUntukDosen("Test Pesan")
                .status(StatusLog.MENUNGGU)
                .idLowongan(idLowongan)
                .idMahasiswa(idMahasiswa)
                .idDosen(idDosen)
                .build();

        assertEquals(id, logFromBuilder.getId());
        assertEquals("Test Log", logFromBuilder.getJudul());
        assertEquals("Keterangan Test", logFromBuilder.getKeterangan());
        assertEquals(KategoriLog.ASISTENSI, logFromBuilder.getKategori());
        assertEquals(LocalDate.of(2025, 3, 15), logFromBuilder.getTanggalLog());
        assertEquals(LocalTime.of(8, 0), logFromBuilder.getWaktuMulai());
        assertEquals(LocalTime.of(10, 0), logFromBuilder.getWaktuSelesai());
        assertEquals("Test Pesan", logFromBuilder.getPesanUntukDosen());
        assertEquals(StatusLog.MENUNGGU, logFromBuilder.getStatus());
        assertEquals(idLowongan, logFromBuilder.getIdLowongan());
        assertEquals(idMahasiswa, logFromBuilder.getIdMahasiswa());
        assertEquals(idDosen, logFromBuilder.getIdDosen());
    }
}