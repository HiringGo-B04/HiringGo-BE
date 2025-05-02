package id.ac.ui.cs.advprog.log.mapper;

import id.ac.ui.cs.advprog.log.dto.LogRequestDTO;
import id.ac.ui.cs.advprog.log.dto.LogResponseDTO;
import id.ac.ui.cs.advprog.log.model.Log;
import id.ac.ui.cs.advprog.log.model.LogBuilder;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class LogMapper {

    public Log toEntity(LogRequestDTO dto) {
        return new LogBuilder()
                .judul(dto.getJudul())
                .keterangan(dto.getKeterangan())
                .kategori(dto.getKategori())
                .tanggalLog(dto.getTanggalLog())
                .waktuMulai(dto.getWaktuMulai())
                .waktuSelesai(dto.getWaktuSelesai())
                .pesanUntukDosen(dto.getPesanUntukDosen())
                .status(dto.getStatus())
                .build();
    }

    public LogResponseDTO toDTO(Log log) {
        LogResponseDTO dto = new LogResponseDTO();
        dto.setId(log.getId() != null ? log.getId() : UUID.randomUUID());
        dto.setJudul(log.getJudul());
        dto.setKeterangan(log.getKeterangan());
        dto.setKategori(log.getKategori());
        dto.setTanggalLog(log.getTanggalLog());
        dto.setWaktuMulai(log.getWaktuMulai());
        dto.setWaktuSelesai(log.getWaktuSelesai());
        dto.setPesanUntukDosen(log.getPesanUntukDosen());
        dto.setStatus(log.getStatus());
        return dto;
    }
}
