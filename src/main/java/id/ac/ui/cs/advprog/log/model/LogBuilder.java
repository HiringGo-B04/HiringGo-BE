package id.ac.ui.cs.advprog.log.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class LogBuilder {

    private final Log log = new Log();

    public LogBuilder judul(String judul) {
        if (judul == null || judul.trim().isEmpty()) {
            throw new IllegalArgumentException("Judul tidak boleh kosong");
        }
        log.setJudul(judul);
        return this;
    }

    public LogBuilder keterangan(String keterangan) {
        log.setKeterangan(keterangan);
        return this;
    }

    public LogBuilder kategori(String kategori) {
        log.setKategori(kategori);
        return this;
    }

    public LogBuilder tanggalLog(LocalDate tanggalLog) {
        if (tanggalLog.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal log tidak boleh di masa depan");
        }
        log.setTanggalLog(tanggalLog);
        return this;
    }

    public LogBuilder waktuMulai(LocalTime waktuMulai) {
        log.setWaktuMulai(waktuMulai);
        return this;
    }

    public LogBuilder waktuSelesai(LocalTime waktuSelesai) {
        log.setWaktuSelesai(waktuSelesai);
        return this;
    }

    public LogBuilder pesanUntukDosen(String pesan) {
        log.setPesanUntukDosen(pesan);
        return this;
    }

    public LogBuilder status(String status) {
        log.setStatus(status);
        return this;
    }

    public Log build() {
        if (log.getWaktuMulai() != null && log.getWaktuSelesai() != null &&
                log.getWaktuSelesai().isBefore(log.getWaktuMulai())) {
            throw new IllegalArgumentException("Waktu selesai harus setelah waktu mulai");
        }

        // 🔥 Tambahan ini untuk memastikan setiap Log punya ID unik
        if (log.getId() == null) {
            log.setId(UUID.randomUUID());
        }

        return log;
    }
}
