package id.ac.ui.cs.advprog.log.service;

import id.ac.ui.cs.advprog.log.dto.LogDTO;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.model.Log;
import java.util.concurrent.CompletableFuture;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LogService {
    // CRUD - Keep synchronous for simple operations
    Log create(Log log);
    List<Log> findAll();
    Log findById(UUID id);
    Log update(UUID id, Log updatedLog);
    void delete(UUID id);

    // Manajemen Log (Mahasiswa) - Add async versions for heavy operations
    List<Log> findByMahasiswaAndLowongan(UUID idMahasiswa, UUID idLowongan);
    CompletableFuture<List<Log>> findByMahasiswaAndLowonganAsync(UUID idMahasiswa, UUID idLowongan);

    Log findByIdForMahasiswa(UUID logId, UUID idMahasiswa);

    Log createLogForMahasiswa(LogDTO logDTO, UUID idMahasiswa);
    CompletableFuture<Log> createLogForMahasiswaAsync(LogDTO logDTO, UUID idMahasiswa);

    Log updateLogForMahasiswa(UUID id, LogDTO logDTO, UUID idMahasiswa);
    void deleteLogForMahasiswa(UUID id, UUID idMahasiswa);

    double calculateHonor(UUID idMahasiswa, UUID idLowongan, int tahun, int bulan);
    CompletableFuture<Double> calculateHonorAsync(UUID idMahasiswa, UUID idLowongan, int tahun, int bulan);

    Map<String, Object> calculateHonorData(UUID idMahasiswa, UUID idLowongan, int tahun, int bulan);
    CompletableFuture<Map<String, Object>> calculateHonorDataAsync(UUID idMahasiswa, UUID idLowongan, int tahun, int bulan);

    // Periksa Log (Dosen) - Add async versions
    List<Log> findByDosen(UUID idDosen);
    CompletableFuture<List<Log>> findByDosenAsync(UUID idDosen);

    Log verifyLog(UUID logId, StatusLog status, UUID idDosen);
    CompletableFuture<Log> verifyLogAsync(UUID logId, StatusLog status, UUID idDosen);

    List<Log> findByLowonganAndDosen(UUID idLowongan, UUID idDosen);
    CompletableFuture<List<Log>> findByLowonganAndDosenAsync(UUID idLowongan, UUID idDosen);

    // Validasi - Keep synchronous for quick validation
    boolean validateLowongan(UUID idLowongan);
    boolean validateMahasiswa(UUID idMahasiswa);
    boolean validateDosen(UUID idDosen);
    boolean isDosenOwnsLowongan(UUID idLowongan, UUID idDosen);
    List<UUID> getDosenIdsByLowonganId(UUID idLowongan);

    // Async validation for heavy operations
    CompletableFuture<Boolean> validateLowonganAsync(UUID idLowongan);
    CompletableFuture<Boolean> validateMahasiswaAsync(UUID idMahasiswa);
    CompletableFuture<Boolean> validateDosenAsync(UUID idDosen);
}