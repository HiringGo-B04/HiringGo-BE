package id.ac.ui.cs.advprog.log.dto;

import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class LogRequestDTO {
    private String judul;
    private String keterangan;
    private KategoriLog kategori;
    private LocalDate tanggalLog;
    private LocalTime waktuMulai;
    private LocalTime waktuSelesai;
    private String pesanUntukDosen;
    private StatusLog status;
}
