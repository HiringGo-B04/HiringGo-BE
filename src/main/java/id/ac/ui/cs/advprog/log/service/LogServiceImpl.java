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
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
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

    // CRUD Methods
    @Override
    public Log create(Log log) {
        return logRepository.save(log);
    }

    @Override
    public List<Log> findAll() {
        return logRepository.findAll();
    }

    @Override
    public Log findById(UUID id) {
        return logRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Log tidak ditemukan dengan id: " + id));
    }

    @Override
    public Log update(UUID id, Log updatedLog) {
        Log existingLog = findById(id);
        // Update logic here
        return logRepository.save(existingLog);
    }

    @Override
    public void delete(UUID id) {
        Log log = findById(id);
        logRepository.delete(log);
    }

    // Manajemen Log (Mahasiswa)

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

        // FIXED: Store all dosen IDs in a special format or just use first dosen
        // For now, we'll use the first dosen as primary, but modify verification logic
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

    // NEW HELPER METHOD - reuse Lowongan object
    private List<UUID> getDosenIdsByLowonganWithLowongan(Lowongan lowongan) {
        String mataKuliahNama = lowongan.getMatkul();

        // Find MataKuliah by nama
        List<MataKuliah> allMataKuliah = mataKuliahRepository.findAll();
        Optional<MataKuliah> mataKuliahOpt = allMataKuliah.stream()
                .filter(mk -> mk.getNama().equals(mataKuliahNama))
                .findFirst();

        if (mataKuliahOpt.isEmpty()) {
            throw new ResourceNotFoundException("Mata kuliah tidak ditemukan: " + mataKuliahNama);
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

        Log updatedLog = new LogBuilder()
                .id(log.getId())
                .judul(logDTO.getJudul())
                .keterangan(logDTO.getKeterangan())
                .kategori(KategoriLog.valueOf(logDTO.getKategori()))
                .tanggalLog(logDTO.getTanggalLog())
                .waktuMulai(logDTO.getWaktuMulai())
                .waktuSelesai(logDTO.getWaktuSelesai())
                .pesanUntukDosen(logDTO.getPesanUntukDosen())
                .status(log.getStatus())
                .idLowongan(log.getIdLowongan())
                .idMahasiswa(log.getIdMahasiswa())
                .idDosen(log.getIdDosen())
                .build();

        return logRepository.save(updatedLog);
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

    // Periksa Log (Dosen)

    @Override
    public List<Log> findByDosen(UUID idDosen) {
        if (!validateDosen(idDosen)) {
            throw new ResourceNotFoundException("Dosen tidak ditemukan");
        }

        // FIXED: Get all logs where dosen is pengampu of the matakuliah
        List<Log> allLogs = new ArrayList<>();

        // Get all lowongan where this dosen is pengampu
        List<Lowongan> allLowongan = lowonganRepository.findAll();
        for (Lowongan lowongan : allLowongan) {
            if (isDosenOwnsLowongan(lowongan.getId(), idDosen)) {
                List<Log> logsForLowongan = logRepository.findByIdLowongan(lowongan.getId());
                allLogs.addAll(logsForLowongan);
            }
        }

        return allLogs;
    }

    @Override
    public Log verifyLog(UUID logId, StatusLog status, UUID idDosen) {
        Log log = findById(logId);

        // FIXED: Check if dosen is pengampu of the matakuliah for this lowongan
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

    // Validation methods

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

    @Override
    public List<UUID> getDosenIdsByLowonganId(UUID idLowongan) {
        // Tracing: Lowongan -> MataKuliah -> DosenPengampu
        Optional<Lowongan> lowonganOpt = lowonganRepository.findById(idLowongan);
        if (lowonganOpt.isEmpty()) {
            throw new ResourceNotFoundException("Lowongan tidak ditemukan");
        }

        Lowongan lowongan = lowonganOpt.get();
        String mataKuliahNama = lowongan.getMatkul();

        // Find MataKuliah by nama
        List<MataKuliah> allMataKuliah = mataKuliahRepository.findAll();
        Optional<MataKuliah> mataKuliahOpt = allMataKuliah.stream()
                .filter(mk -> mk.getNama().equals(mataKuliahNama))
                .findFirst();

        if (mataKuliahOpt.isEmpty()) {
            throw new ResourceNotFoundException("Mata kuliah tidak ditemukan: " + mataKuliahNama);
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

    private boolean isMahasiswaAcceptedForLowongan(UUID idMahasiswa, UUID idLowongan) {
        return lamaranRepository.findAll().stream()
                .anyMatch(lamaran ->
                        lamaran.getIdMahasiswa().equals(idMahasiswa) &&
                                lamaran.getIdLowongan().equals(idLowongan) &&
                                lamaran.getStatus() == id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran.DITERIMA
                );
    }
}