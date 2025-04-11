package id.ac.ui.cs.advprog.log.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Log {

    private String judul;
    private String keterangan;
    private String kategori;
    private LocalDate tanggalLog;
    private LocalTime waktuMulai;
    private LocalTime waktuSelesai;
    private String pesanUntukDosen;
    private String status;

    // Getters & Setters
    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public LocalDate getTanggalLog() {
        return tanggalLog;
    }

    public void setTanggalLog(LocalDate tanggalLog) {
        this.tanggalLog = tanggalLog;
    }

    public LocalTime getWaktuMulai() {
        return waktuMulai;
    }

    public void setWaktuMulai(LocalTime waktuMulai) {
        this.waktuMulai = waktuMulai;
    }

    public LocalTime getWaktuSelesai() {
        return waktuSelesai;
    }

    public void setWaktuSelesai(LocalTime waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
    }

    public String getPesanUntukDosen() {
        return pesanUntukDosen;
    }

    public void setPesanUntukDosen(String pesanUntukDosen) {
        this.pesanUntukDosen = pesanUntukDosen;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}