package id.ac.ui.cs.advprog.log.model;

import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LogBuilderTest {

    private Log log;
    private UUID idLowongan;
    private UUID idMahasiswa;

    @BeforeEach
    void setUp() {
        idLowongan = UUID.randomUUID();
        idMahasiswa = UUID.randomUUID();

        log = new LogBuilder()
                .judul("Asistensi Minggu 1")
                .keterangan("Membantu asistensi SDA")
                .kategori(KategoriLog.ASISTENSI)
                .tanggalLog(LocalDate.of(2025, 4, 6))
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .pesanUntukDosen("Upload tugas selesai")
                .status(StatusLog.MENUNGGU)
                .idLowongan(idLowongan)
                .idMahasiswa(idMahasiswa)
                .build();
    }

    @Test
    void testBuildLog_Success() {
        assertNotNull(log);
        assertEquals("Asistensi Minggu 1", log.getJudul());
        assertEquals(KategoriLog.ASISTENSI, log.getKategori());
        assertEquals(StatusLog.MENUNGGU, log.getStatus());
        assertEquals(LocalTime.of(10, 0), log.getWaktuMulai());
        assertEquals(LocalTime.of(12, 0), log.getWaktuSelesai());
        assertEquals("Upload tugas selesai", log.getPesanUntukDosen());
        assertEquals(idLowongan, log.getIdLowongan());
        assertEquals(idMahasiswa, log.getIdMahasiswa());
    }

    @Test
    void testBuildLog_EmptyJudul_ShouldThrowException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new LogBuilder()
                    .judul("")
                    .keterangan("Deskripsi kosong")
                    .kategori(KategoriLog.MENGAWAS)
                    .tanggalLog(LocalDate.now())
                    .waktuMulai(LocalTime.of(9, 0))
                    .waktuSelesai(LocalTime.of(10, 0))
                    .status(StatusLog.MENUNGGU)
                    .idLowongan(UUID.randomUUID())
                    .idMahasiswa(UUID.randomUUID())
                    .build();
        });
        assertTrue(ex.getMessage().contains("Judul tidak boleh kosong"));
    }

    @Test
    void testBuildLog_TanggalMasaDepan_ShouldThrowException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new LogBuilder()
                    .judul("Log Masa Depan")
                    .keterangan("Tidak valid")
                    .kategori(KategoriLog.RAPAT)
                    .tanggalLog(LocalDate.now().plusDays(1))
                    .waktuMulai(LocalTime.of(9, 0))
                    .waktuSelesai(LocalTime.of(11, 0))
                    .status(StatusLog.MENUNGGU)
                    .idLowongan(UUID.randomUUID())
                    .idMahasiswa(UUID.randomUUID())
                    .build();
        });
        assertTrue(ex.getMessage().contains("Tanggal log tidak boleh di masa depan"));
    }

    @Test
    void testBuildLog_WaktuSelesaiBeforeMulai_ShouldThrowException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new LogBuilder()
                    .judul("Log Salah")
                    .keterangan("Waktu tidak valid")
                    .kategori(KategoriLog.MENGOREKSI)
                    .tanggalLog(LocalDate.now())
                    .waktuMulai(LocalTime.of(14, 0))
                    .waktuSelesai(LocalTime.of(13, 0))
                    .status(StatusLog.MENUNGGU)
                    .idLowongan(UUID.randomUUID())
                    .idMahasiswa(UUID.randomUUID())
                    .build();
        });
        assertTrue(ex.getMessage().contains("Waktu selesai harus setelah waktu mulai"));
    }

    @Test
    void testBuildLog_MissingIdLowongan_ShouldThrowException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new LogBuilder()
                    .judul("Log Valid")
                    .keterangan("Tetapi tanpa lowongan")
                    .kategori(KategoriLog.ASISTENSI)
                    .tanggalLog(LocalDate.now())
                    .waktuMulai(LocalTime.of(9, 0))
                    .waktuSelesai(LocalTime.of(11, 0))
                    .status(StatusLog.MENUNGGU)
                    .idMahasiswa(UUID.randomUUID())
                    .build();
        });
        assertTrue(ex.getMessage().contains("ID Lowongan tidak boleh kosong"));
    }

    @Test
    void testBuildLog_MissingIdMahasiswa_ShouldThrowException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new LogBuilder()
                    .judul("Log Valid")
                    .keterangan("Tetapi tanpa mahasiswa")
                    .kategori(KategoriLog.ASISTENSI)
                    .tanggalLog(LocalDate.now())
                    .waktuMulai(LocalTime.of(9, 0))
                    .waktuSelesai(LocalTime.of(11, 0))
                    .status(StatusLog.MENUNGGU)
                    .idLowongan(UUID.randomUUID())
                    .build();
        });
        assertTrue(ex.getMessage().contains("ID Mahasiswa tidak boleh kosong"));
    }
}