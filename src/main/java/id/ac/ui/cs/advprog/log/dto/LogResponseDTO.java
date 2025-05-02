package id.ac.ui.cs.advprog.log.dto;

import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogResponseDTO {
    private UUID id;
    private String judul;
    private String keterangan;
    private KategoriLog kategori;
    private LocalDate tanggalLog;
    private LocalTime waktuMulai;
    private LocalTime waktuSelesai;
    private String pesanUntukDosen;
    private StatusLog status;
}
