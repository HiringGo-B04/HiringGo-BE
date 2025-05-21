package id.ac.ui.cs.advprog.mendaftarlowongan.dto;


import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LamaranDTO {
    private int sks;
    private float ipk;
    private UUID idMahasiswa;
    private UUID idLowongan;
}