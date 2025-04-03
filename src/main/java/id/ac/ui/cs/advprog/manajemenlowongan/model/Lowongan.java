package id.ac.ui.cs.advprog.manajemenlowongan.model;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Lowongan {
    private UUID id;
    private String matkul;
    private int year;
    private String term;
    private int totalAsdosNeeded;
    private int totalAsdosRegistered;
    private int totalAsdosAccepted;
}
