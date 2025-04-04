package id.ac.ui.cs.advprog.manajemenlowongan.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class Lamaran {
    private UUID id;
    private UUID idMahasiswa;
    private UUID idLowongan;
    private int sks;
    private float gpa;
    private StatusLamaran status;

    public void updateStatus(StatusLamaran status) {
        this.status = status;
    }
}

enum StatusLamaran {
    MENUNGGU, DITERIMA, DITOLAK;
}