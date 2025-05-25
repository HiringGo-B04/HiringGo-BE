package id.ac.ui.cs.advprog.log.service;

import id.ac.ui.cs.advprog.log.dto.LogDTO;
import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.exception.BadRequestException;
import id.ac.ui.cs.advprog.log.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.log.exception.ForbiddenException;
import id.ac.ui.cs.advprog.log.model.Log;
import id.ac.ui.cs.advprog.log.model.LogBuilder;
import id.ac.ui.cs.advprog.log.repository.LogRepository;
import id.ac.ui.cs.advprog.log.service.command.ApproveLogCommand;
import id.ac.ui.cs.advprog.log.service.command.LogCommand;
import id.ac.ui.cs.advprog.log.service.command.RejectLogCommand;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.mendaftarlowongan.repository.LamaranRepository;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Transactional
public class LogServiceImpl implements LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private LowonganRepository lowonganRepository;

    @Autowired
    private LamaranRepository lamaranRepository;

    @Autowired
    private MataKuliahRepository mataKuliahRepository;

    @Autowired
    private UserRepository userRepository;

//    // Inject the custom task executor
//    @Autowired
//    @Qualifier("taskExecutor")
//    private Executor taskExecutor;

    // CRUD Methods - Keep synchronous for simple operations
    @Override
    public Log create(Log log) {
        return logRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Log> findAll() {
        return logRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Log findById(UUID id) {
        return logRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Log tidak ditemukan dengan id: " + id));
    }

    @Override
    public Log update(UUID id, Log updatedLog) {
        Log existingLog = findById(id);

        // Update fields
        existingLog.setJudul(updatedLog.getJudul());
        existingLog.setKeterangan(updatedLog.getKeterangan());
        existingLog.setKategori(updatedLog.getKategori());
        existingLog.setTanggalLog(updatedLog.getTanggalLog());
        existingLog.setWaktuMulai(updatedLog.getWaktuMulai());
        existingLog.setWaktuSelesai(updatedLog.getWaktuSelesai());
        existingLog.setPesanUntukDosen(updatedLog.getPesanUntukDosen());
        existingLog.setStatus(updatedLog.getStatus());

        return logRepository.save(existingLog);
    }

    @Override
    public void delete(UUID id) {
        Log log = findById(id);
        logRepository.delete(log);
    }

    // Manajemen Log (Mahasiswa) - Synchronous versions

    @Override
    public List<Log> findByMahasiswaAndLowongan(UUID idMahasiswa, UUID idLowongan) {
        if (!validateMahasiswa(idMahasiswa)) {
            throw new ResourceNotFoundException("Mahasiswa tidak ditemukan");
        }
        if (!validateLowongan(idLowongan)) {
            throw new ResourceNotFoundException("Lowongan tidak ditemukan");
        }

        if (!isMahasiswaAcceptedForLowongan(idMahasiswa, idLowongan)) {
            throw new ForbiddenException("Anda belum diterima pada lowongan ini");
        }

        return logRepository.findByIdMahasiswaAndIdLowongan(idMahasiswa, idLowongan);
    }

//    // Async version using custom executor
//    @Override
//    public CompletableFuture<List<Log>> findByMahasiswaAndLowonganAsync(UUID idMahasiswa, UUID idLowongan) {
//        return CompletableFuture.supplyAsync(() -> {
//            if (!validateMahasiswa(idMahasiswa)) {
//                throw new ResourceNotFoundException("Mahasiswa tidak ditemukan");
//            }
//            if (!validateLowongan(idLowongan)) {
//                throw new ResourceNotFoundException("Lowongan tidak ditemukan");
//            }
//
//            if (!isMahasiswaAcceptedForLowongan(idMahasiswa, idLowongan)) {
//                throw new ForbiddenException("Anda belum diterima pada lowongan ini");
//            }
//
//            return logRepository.findByIdMahasiswaAndIdLowongan(idMahasiswa, idLowongan);
//        }, taskExecutor);
//    }

    @Override
    public Log findByIdForMahasiswa(UUID logId, UUID idMahasiswa) {
        Log log = findById(logId);

        if (!log.getIdMahasiswa().equals(idMahasiswa)) {
            throw new ForbiddenException("Anda tidak dapat mengakses log milik mahasiswa lain");
        }

        return log;
    }

    @Override
    public Log createLogForMahasiswa(LogDTO logDTO, UUID idMahasiswa) {
        // Validasi mahasiswa
        if (!validateMahasiswa(idMahasiswa)) {
            throw new ResourceNotFoundException("Mahasiswa tidak ditemukan");
        }

        // Validasi lowongan
        Optional<Lowongan> lowonganOpt = lowonganRepository.findById(logDTO.getIdLowongan());
        if (lowonganOpt.isEmpty()) {
            throw new ResourceNotFoundException("Lowongan tidak ditemukan");
        }
        Lowongan lowongan = lowonganOpt.get();

        // Validasi mahasiswa sudah diterima di lowongan
        if (!isMahasiswaAcceptedForLowongan(idMahasiswa, logDTO.getIdLowongan())) {
            throw new BadRequestException("Mahasiswa belum diterima pada lowongan ini");
        }

        // Validasi tanggal log tidak boleh masa depan
        if (logDTO.getTanggalLog().isAfter(LocalDate.now())) {
            throw new BadRequestException("Tanggal log tidak boleh di masa depan");
        }

        // Get dosen from lowongan -> matakuliah -> dosenpengampu - FETCH ONCE
        List<UUID> dosenIds = getDosenIdsByLowonganWithLowongan(lowongan);
        if (dosenIds.isEmpty()) {
            throw new BadRequestException("Tidak ada dosen pengampu untuk lowongan ini");
        }

        UUID idDosen = dosenIds.get(0);

        // Validate the selected dosen exists
        if (!validateDosen(idDosen)) {
            throw new ResourceNotFoundException("Dosen tidak ditemukan");
        }

        Log log = new LogBuilder()
                .judul(logDTO.getJudul())
                .keterangan(logDTO.getKeterangan())
                .kategori(KategoriLog.valueOf(logDTO.getKategori()))
                .tanggalLog(logDTO.getTanggalLog())
                .waktuMulai(logDTO.getWaktuMulai())
                .waktuSelesai(logDTO.getWaktuSelesai())
                .pesanUntukDosen(logDTO.getPesanUntukDosen())
                .status(StatusLog.MENUNGGU)
                .idLowongan(logDTO.getIdLowongan())
                .idMahasiswa(idMahasiswa)
                .idDosen(idDosen)
                .build();

        return logRepository.save(log);
    }

//    // Async version using custom executor
//    @Override
//    public CompletableFuture<Log> createLogForMahasiswaAsync(LogDTO logDTO, UUID idMahasiswa) {
//        return CompletableFuture.supplyAsync(() -> {
//            return createLogForMahasiswa(logDTO, idMahasiswa);
//        }, taskExecutor);
//    }

    private List<UUID> getDosenIdsByLowonganWithLowongan(Lowongan lowongan) {
        String mataKuliahKode = lowongan.getMatkul();

        List<MataKuliah> allMataKuliah = mataKuliahRepository.findAll();
        Optional<MataKuliah> mataKuliahOpt = allMataKuliah.stream()
                .filter(mk -> mk.getKode().equals(mataKuliahKode))
                .findFirst();

        if (mataKuliahOpt.isEmpty()) {
            throw new ResourceNotFoundException("Mata kuliah tidak ditemukan dengan kode: " + mataKuliahKode);
        }

        MataKuliah mataKuliah = mataKuliahOpt.get();

        return mataKuliah.getDosenPengampu().stream()
                .map(User::getUserId)
                .collect(Collectors.toList());
    }

    @Override
    public Log updateLogForMahasiswa(UUID id, LogDTO logDTO, UUID idMahasiswa) {
        Log log = findByIdForMahasiswa(id, idMahasiswa);

        if (log.getStatus() != StatusLog.MENUNGGU) {
            throw new BadRequestException("Tidak dapat mengubah log yang sudah diverifikasi");
        }

        if (logDTO.getTanggalLog().isAfter(LocalDate.now())) {
            throw new BadRequestException("Tanggal log tidak boleh di masa depan");
        }

        log.setJudul(logDTO.getJudul());
        log.setKeterangan(logDTO.getKeterangan());
        log.setKategori(KategoriLog.valueOf(logDTO.getKategori()));
        log.setTanggalLog(logDTO.getTanggalLog());
        log.setWaktuMulai(logDTO.getWaktuMulai());
        log.setWaktuSelesai(logDTO.getWaktuSelesai());
        log.setPesanUntukDosen(logDTO.getPesanUntukDosen());

        return logRepository.save(log);
    }

    @Override
    public void deleteLogForMahasiswa(UUID id, UUID idMahasiswa) {
        Log log = findByIdForMahasiswa(id, idMahasiswa);

        if (log.getStatus() != StatusLog.MENUNGGU) {
            throw new BadRequestException("Tidak dapat menghapus log yang sudah diverifikasi");
        }

        logRepository.delete(log);
    }

    @Override
    public double calculateHonor(UUID idMahasiswa, UUID idLowongan, int tahun, int bulan) {
        if (!validateMahasiswa(idMahasiswa)) {
            throw new ResourceNotFoundException("Mahasiswa tidak ditemukan");
        }
        if (!validateLowongan(idLowongan)) {
            throw new ResourceNotFoundException("Lowongan tidak ditemukan");
        }

        List<Log> acceptedLogs = logRepository.findAcceptedLogsByMahasiswaAndLowonganAndMonth(
                idMahasiswa, idLowongan, StatusLog.DITERIMA, tahun, bulan);

        double totalHoursWorked = 0;
        for (Log log : acceptedLogs) {
            Duration duration = Duration.between(log.getWaktuMulai(), log.getWaktuSelesai());
            double hours = duration.toMinutes() / 60.0;
            totalHoursWorked += hours;
        }

        return totalHoursWorked * 27500;
    }

//    // Async version using custom executor
//    @Override
//    public CompletableFuture<Double> calculateHonorAsync(UUID idMahasiswa, UUID idLowongan, int tahun, int bulan) {
//        return CompletableFuture.supplyAsync(() -> {
//            return calculateHonor(idMahasiswa, idLowongan, tahun, bulan);
//        }, taskExecutor);
//    }

    @Override
    public Map<String, Object> calculateHonorData(UUID idMahasiswa, UUID idLowongan, int tahun, int bulan) {
        double honor = calculateHonor(idMahasiswa, idLowongan, tahun, bulan);

        Map<String, Object> response = new HashMap<>();
        response.put("bulan", bulan);
        response.put("tahun", tahun);
        response.put("lowonganId", idLowongan);
        response.put("honor", honor);
        response.put("formattedHonor", String.format("Rp %,.2f", honor));

        return response;
    }

//    // Async version using custom executor
//    @Override
//    public CompletableFuture<Map<String, Object>> calculateHonorDataAsync(UUID idMahasiswa, UUID idLowongan, int tahun, int bulan) {
//        return CompletableFuture.supplyAsync(() -> {
//            return calculateHonorData(idMahasiswa, idLowongan, tahun, bulan);
//        }, taskExecutor);
//    }

    // Periksa Log (Dosen)
    @Override
    public List<Log> findByDosen(UUID idDosen) {
        if (!validateDosen(idDosen)) {
            throw new ResourceNotFoundException("Dosen tidak ditemukan");
        }

        // SIMPLIFIED: Just return all logs where idDosen matches
        // This works because when creating log, we already set idDosen
        // based on dosen pengampu of the mata kuliah
        return logRepository.findByIdDosen(idDosen);
    }

//    // Async version with parallel processing using custom executor
//    @Override
//    public CompletableFuture<List<Log>> findByDosenAsync(UUID idDosen) {
//        return CompletableFuture.supplyAsync(() -> {
//            if (!validateDosen(idDosen)) {
//                throw new ResourceNotFoundException("Dosen tidak ditemukan");
//            }
//
//            List<Lowongan> allLowongan = lowonganRepository.findAll();
//
//            // Process lowongan in parallel using custom executor
//            List<CompletableFuture<List<Log>>> futures = new ArrayList<>();
//
//            for (Lowongan lowongan : allLowongan) {
//                CompletableFuture<List<Log>> future = CompletableFuture.supplyAsync(() -> {
//                    if (isDosenOwnsLowongan(lowongan.getId(), idDosen)) {
//                        return logRepository.findByIdLowongan(lowongan.getId());
//                    }
//                    return new ArrayList<>();
//                }, taskExecutor);
//                futures.add(future);
//            }
//
//            // Combine all results
//            List<Log> allLogs = new ArrayList<>();
//            for (CompletableFuture<List<Log>> future : futures) {
//                allLogs.addAll(future.join());
//            }
//
//            return allLogs;
//        }, taskExecutor);
//    }

    @Override
    public Log verifyLog(UUID logId, StatusLog status, UUID idDosen) {
        Log log = findById(logId);

        // Check if dosen is pengampu of the matakuliah for this lowongan
        if (!isDosenOwnsLowongan(log.getIdLowongan(), idDosen)) {
            throw new ForbiddenException("Anda tidak dapat memverifikasi log untuk lowongan ini");
        }

        if (status != StatusLog.DITERIMA && status != StatusLog.DITOLAK) {
            throw new BadRequestException("Status tidak valid. Gunakan DITERIMA atau DITOLAK.");
        }

        if (log.getStatus() != StatusLog.MENUNGGU) {
            throw new BadRequestException("Log ini sudah diverifikasi sebelumnya");
        }

        LogCommand command;
        if (status == StatusLog.DITERIMA) {
            command = new ApproveLogCommand(log);
        } else {
            command = new RejectLogCommand(log);
        }

        command.execute();
        return logRepository.save(log);
    }

//    // Async version using custom executor
//    @Override
//    public CompletableFuture<Log> verifyLogAsync(UUID logId, StatusLog status, UUID idDosen) {
//        return CompletableFuture.supplyAsync(() -> {
//            return verifyLog(logId, status, idDosen);
//        }, taskExecutor);
//    }

    @Override
    public List<Log> findByLowonganAndDosen(UUID idLowongan, UUID idDosen) {
        if (!validateLowongan(idLowongan)) {
            throw new ResourceNotFoundException("Lowongan tidak ditemukan");
        }
        if (!validateDosen(idDosen)) {
            throw new ResourceNotFoundException("Dosen tidak ditemukan");
        }

        if (!isDosenOwnsLowongan(idLowongan, idDosen)) {
            throw new ForbiddenException("Dosen tidak dapat mengakses log untuk lowongan ini");
        }

        return logRepository.findByIdLowongan(idLowongan);
    }

//    // Async version using custom executor
//    @Override
//    public CompletableFuture<List<Log>> findByLowonganAndDosenAsync(UUID idLowongan, UUID idDosen) {
//        return CompletableFuture.supplyAsync(() -> {
//            return findByLowonganAndDosen(idLowongan, idDosen);
//        }, taskExecutor);
//    }

    // Validation methods - Keep synchronous for quick validation

    @Override
    public boolean validateLowongan(UUID idLowongan) {
        return lowonganRepository.findById(idLowongan).isPresent();
    }

    @Override
    public boolean validateMahasiswa(UUID idMahasiswa) {
        Optional<User> mahasiswa = userRepository.findById(idMahasiswa);
        return mahasiswa.isPresent() && "STUDENT".equals(mahasiswa.get().getRole());
    }

    @Override
    public boolean validateDosen(UUID idDosen) {
        Optional<User> dosen = userRepository.findById(idDosen);
        return dosen.isPresent() && "LECTURER".equals(dosen.get().getRole());
    }

//    // Async validation methods using custom executor
//    @Override
//    public CompletableFuture<Boolean> validateLowonganAsync(UUID idLowongan) {
//        return CompletableFuture.supplyAsync(() -> validateLowongan(idLowongan), taskExecutor);
//    }
//
//    @Override
//    public CompletableFuture<Boolean> validateMahasiswaAsync(UUID idMahasiswa) {
//        return CompletableFuture.supplyAsync(() -> validateMahasiswa(idMahasiswa), taskExecutor);
//    }
//
//    @Override
//    public CompletableFuture<Boolean> validateDosenAsync(UUID idDosen) {
//        return CompletableFuture.supplyAsync(() -> validateDosen(idDosen), taskExecutor);
//    }

    @Override
    public List<UUID> getDosenIdsByLowonganId(UUID idLowongan) {
        Optional<Lowongan> lowonganOpt = lowonganRepository.findById(idLowongan);
        if (lowonganOpt.isEmpty()) {
            throw new ResourceNotFoundException("Lowongan tidak ditemukan");
        }

        Lowongan lowongan = lowonganOpt.get();
        String mataKuliahKode = lowongan.getMatkul();

        List<MataKuliah> allMataKuliah = mataKuliahRepository.findAll();
        Optional<MataKuliah> mataKuliahOpt = allMataKuliah.stream()
                .filter(mk -> mk.getKode().equals(mataKuliahKode))
                .findFirst();

        if (mataKuliahOpt.isEmpty()) {
            throw new ResourceNotFoundException("Mata kuliah tidak ditemukan dengan kode: " + mataKuliahKode);
        }

        MataKuliah mataKuliah = mataKuliahOpt.get();

        // Get all dosen pengampu
        return mataKuliah.getDosenPengampu().stream()
                .map(User::getUserId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isDosenOwnsLowongan(UUID idLowongan, UUID idDosen) {
        if (!validateDosen(idDosen)) {
            return false;
        }

        if (!validateLowongan(idLowongan)) {
            return false;
        }

        try {
            // Check if dosen is pengampu of the matakuliah for this lowongan
            List<UUID> dosenIds = getDosenIdsByLowonganId(idLowongan);
            return dosenIds.contains(idDosen);
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isMahasiswaAcceptedForLowongan(UUID idMahasiswa, UUID idLowongan) {
        return lamaranRepository.findAll().stream()
                .anyMatch(lamaran ->
                        lamaran.getIdMahasiswa().equals(idMahasiswa) &&
                                lamaran.getIdLowongan().equals(idLowongan) &&
                                lamaran.getStatus() == id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran.DITERIMA
                );
    }
}