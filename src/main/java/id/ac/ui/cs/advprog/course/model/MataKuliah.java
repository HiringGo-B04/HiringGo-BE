package id.ac.ui.cs.advprog.course.model;

import java.util.ArrayList;
import java.util.List;

// untuk sementara masih belum ada mikroservice
public class MataKuliah {

    private String kode;

    private String nama;

    private String deskripsi;

    private int sks;

    private List<String> dosenPengampu;

    // Constructor kosong (untuk framework nanti)
    public MataKuliah() {
        this.dosenPengampu = new ArrayList<>();
    }

    // Constructor penuh
    public MataKuliah(String kode, String nama, String deskripsi, int sks) {
        this.kode = kode;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.sks = sks;
        this.dosenPengampu = new ArrayList<>();
    }

    // get & set

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public int getSks() {
        return sks;
    }

    public void setSks(int sks) {
        this.sks = sks;
    }

    public List<String> getDosenPengampu() {
        return dosenPengampu;
    }

    public void setDosenPengampu(List<String> dosenPengampu) {
        this.dosenPengampu = dosenPengampu;
    }

    // Tambahkan helper method untuk menambah dosen satu per satu: (masih di tes)
    public void addDosenPengampu(String namaDosen) {
        if (this.dosenPengampu == null) {
            this.dosenPengampu = new ArrayList<>();
        }
        this.dosenPengampu.add(namaDosen);
    }
}

