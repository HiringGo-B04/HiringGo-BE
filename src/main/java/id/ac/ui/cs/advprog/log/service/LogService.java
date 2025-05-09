package id.ac.ui.cs.advprog.log.service;

import id.ac.ui.cs.advprog.log.dto.LogDTO;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.model.Log;

import java.util.List;
import java.util.UUID;

public interface LogService {
    // CRUD
    Log create(Log log);
    List<Log> findAll();
    Log findById(UUID id);
    Log update(UUID id, Log updatedLog);
    void delete(UUID id);

    // Manajemen Log (Mahasiswa)
    List<Log> findByMahasiswaAndLowongan(UUID idMahasiswa, UUID idLowongan);
    Log createLogForMahasiswa(LogDTO logDTO, UUID idMahasiswa, UUID idDosen);
    Log updateLogForMahasiswa(UUID id, LogDTO logDTO, UUID idMahasiswa);
    void deleteLogForMahasiswa(UUID id, UUID idMahasiswa);
    double calculateHonor(UUID idMahasiswa, UUID idLowongan, int tahun, int bulan);

    // Periksa Log (Dosen)
    List<Log> findByDosen(UUID idDosen);
    Log verifyLog(UUID logId, StatusLog status, UUID idDosen);
    List<Log> findByLowonganAndDosen(UUID idLowongan, UUID idDosen);

    // Validasi
    boolean validateLowongan(UUID idLowongan);
    boolean validateMahasiswa(UUID idMahasiswa);
    boolean validateDosen(UUID idDosen);
    boolean isDosenOwnsLowongan(UUID idLowongan, UUID idDosen);
}