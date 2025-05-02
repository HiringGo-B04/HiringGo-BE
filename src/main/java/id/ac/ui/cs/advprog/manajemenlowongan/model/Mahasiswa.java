package id.ac.ui.cs.advprog.manajemenlowongan.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Mahasiswa extends User {
    private String fullName;
    private String nim;

    public Mahasiswa() {
        super();
    }

    public Mahasiswa(String fullName, String nim) {
        super();
        this.fullName = fullName;
        this.nim = nim;
    }
}