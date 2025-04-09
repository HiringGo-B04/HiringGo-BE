import java.time.LocalDate;
import java.time.LocalTime;

public class Log {

    private String judul;
    private String keterangan;
    private Object kategori; // belum pakai enum
    private LocalDate tanggalLog;
    private LocalTime waktuMulai;
    private LocalTime waktuSelesai;
    private String pesanUntukDosen;
    private Object status; // belum pakai enum

    // Buat constructor private agar hanya bisa dibuat via builder
    private Log() {}

    public static LogBuilder builder() {
        return new LogBuilder();
    }

    public String getJudul() {
        return judul;
    }

    public Object getStatus() {
        return status;
    }

    // --- Inner class: LogBuilder (skeleton only) ---
    public static class LogBuilder {
        public LogBuilder judul(String judul) {
            return this;
        }

        public LogBuilder keterangan(String keterangan) {
            return this;
        }

        public LogBuilder kategori(Object kategori) {
            return this;
        }

        public LogBuilder tanggalLog(LocalDate tanggal) {
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

        public LogBuilder status(Object status) {
            return this;
        }

        public Log build() {
            return new Log();
        }
    }
}
