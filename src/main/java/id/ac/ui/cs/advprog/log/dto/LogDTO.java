package id.ac.ui.cs.advprog.log.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class LogDTO {
    private String judul;
    private String keterangan;
    private String kategori;
    private LocalDate tanggalLog;
    private LocalTime waktuMulai;
    private LocalTime waktuSelesai;
    private String pesanUntukDosen;
    private UUID idLowongan;
}