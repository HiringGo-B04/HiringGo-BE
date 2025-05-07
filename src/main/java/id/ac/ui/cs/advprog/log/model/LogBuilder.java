package id.ac.ui.cs.advprog.log.model;

import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
public class LogBuilder {
    private UUID id;
    private String judul;
    private String keterangan;
    private KategoriLog kategori;
    private LocalDate tanggalLog;
    private LocalTime waktuMulai;
    private LocalTime waktuSelesai;
    private String pesanUntukDosen;
    private StatusLog status = StatusLog.MENUNGGU;
    private UUID idLowongan;
    private UUID idMahasiswa;
    private UUID idDosen;

    public LogBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public LogBuilder judul(String judul) {
        if (judul == null || judul.trim().isEmpty()) {
            throw new IllegalArgumentException("Judul tidak boleh kosong");
        }
        this.judul = judul;
        return this;
    }

    public LogBuilder keterangan(String keterangan) {
        this.keterangan = keterangan;
        return this;
    }

    public LogBuilder kategori(KategoriLog kategori) {
        if (kategori == null) {
            throw new IllegalArgumentException("Kategori tidak boleh kosong");
        }
        this.kategori = kategori;
        return this;
    }

    public LogBuilder tanggalLog(LocalDate tanggalLog) {
        if (tanggalLog == null) {
            throw new IllegalArgumentException("Tanggal log tidak boleh kosong");
        }
        if (tanggalLog.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal log tidak boleh di masa depan");
        }
        this.tanggalLog = tanggalLog;
        return this;
    }

    public LogBuilder waktuMulai(LocalTime waktuMulai) {
        if (waktuMulai == null) {
            throw new IllegalArgumentException("Waktu mulai tidak boleh kosong");
        }
        this.waktuMulai = waktuMulai;
        return this;
    }

    public LogBuilder waktuSelesai(LocalTime waktuSelesai) {
        if (waktuSelesai == null) {
            throw new IllegalArgumentException("Waktu selesai tidak boleh kosong");
        }
        this.waktuSelesai = waktuSelesai;
        return this;
    }

    public LogBuilder pesanUntukDosen(String pesan) {
        this.pesanUntukDosen = pesan;
        return this;
    }

    public LogBuilder status(StatusLog status) {
        this.status = status;
        return this;
    }

    public LogBuilder idLowongan(UUID idLowongan) {
        if (idLowongan == null) {
            throw new IllegalArgumentException("ID Lowongan tidak boleh kosong");
        }
        this.idLowongan = idLowongan;
        return this;
    }

    public LogBuilder idMahasiswa(UUID idMahasiswa) {
        if (idMahasiswa == null) {
            throw new IllegalArgumentException("ID Mahasiswa tidak boleh kosong");
        }
        this.idMahasiswa = idMahasiswa;
        return this;
    }

    public LogBuilder idDosen(UUID idDosen) {
        this.idDosen = idDosen;
        return this;
    }

    public Log build() {
        validateBeforeBuild();
        return new Log(this);
    }

    private void validateBeforeBuild() {
        if (this.judul == null || this.judul.trim().isEmpty()) {
            throw new IllegalArgumentException("Judul tidak boleh kosong");
        }

        if (this.kategori == null) {
            throw new IllegalArgumentException("Kategori tidak boleh kosong");
        }

        if (this.tanggalLog == null) {
            throw new IllegalArgumentException("Tanggal log tidak boleh kosong");
        }

        if (this.waktuMulai == null) {
            throw new IllegalArgumentException("Waktu mulai tidak boleh kosong");
        }

        if (this.waktuSelesai == null) {
            throw new IllegalArgumentException("Waktu selesai tidak boleh kosong");
        }

        if (this.waktuSelesai.isBefore(this.waktuMulai)) {
            throw new IllegalArgumentException("Waktu selesai harus setelah waktu mulai");
        }

        if (this.idLowongan == null) {
            throw new IllegalArgumentException("ID Lowongan tidak boleh kosong");
        }

        if (this.idMahasiswa == null) {
            throw new IllegalArgumentException("ID Mahasiswa tidak boleh kosong");
        }
    }
}