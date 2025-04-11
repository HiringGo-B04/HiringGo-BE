package id.ac.ui.cs.advprog.log.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class LogBuilder {

    private final Log log = new Log();

    public LogBuilder judul(String judul) {
        return this;
    }

    public LogBuilder keterangan(String keterangan) {
        return this;
    }

    public LogBuilder kategori(String kategori) {
        return this;
    }

    public LogBuilder tanggalLog(LocalDate tanggalLog) {
        return this;
    }

    public LogBuilder waktuMulai(LocalTime waktuMulai) {
        return this;
    }

    public LogBuilder waktuSelesai(LocalTime waktuSelesai) {
        return this;
    }

    public LogBuilder pesanUntukDosen(String pesan) {
        return this;
    }

    public LogBuilder status(String status) {
        return this;
    }

    public Log build() {
        return log;
    }
}
