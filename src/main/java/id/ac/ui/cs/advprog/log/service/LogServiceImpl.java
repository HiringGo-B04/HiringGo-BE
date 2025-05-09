package id.ac.ui.cs.advprog.log.service;

import id.ac.ui.cs.advprog.log.dto.LogDTO;
import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.exception.BadRequestException;
import id.ac.ui.cs.advprog.log.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.log.model.Log;
import id.ac.ui.cs.advprog.log.model.LogBuilder;
import id.ac.ui.cs.advprog.log.repository.LogRepository;
import id.ac.ui.cs.advprog.log.service.command.ApproveLogCommand;
import id.ac.ui.cs.advprog.log.service.command.LogCommand;
import id.ac.ui.cs.advprog.log.service.command.RejectLogCommand;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.repository.LamaranRepository;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private LowonganRepository lowonganRepository;

    @Autowired
    private LamaranRepository lamaranRepository;

    @Autowired
    private UserRepository userRepository;

    //CRUD
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

        existingLog.setJudul(updatedLog.getJudul());
        existingLog.setKeterangan(updatedLog.getKeterangan());
        existingLog.setKategori(updatedLog.getKategori());
        existingLog.setTanggalLog(updatedLog.getTanggalLog());
        existingLog.setWaktuMulai(updatedLog.getWaktuMulai());
        existingLog.setWaktuSelesai(updatedLog.getWaktuSelesai());
        existingLog.setPesanUntukDosen(updatedLog.getPesanUntukDosen());
        existingLog.setStatus(updatedLog.getStatus());
        existingLog.setIdLowongan(updatedLog.getIdLowongan());
        existingLog.setIdMahasiswa(updatedLog.getIdMahasiswa());
        existingLog.setIdDosen(updatedLog.getIdDosen());

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
        validateMahasiswa(idMahasiswa);
        validateLowongan(idLowongan);
        return logRepository.findByIdMahasiswaAndIdLowongan(idMahasiswa, idLowongan);
    }

    @Override
    public Log createLogForMahasiswa(LogDTO logDTO, UUID idMahasiswa, UUID idDosen) {
        if (!validateMahasiswa(idMahasiswa)) {
            throw new ResourceNotFoundException("Mahasiswa tidak ditemukan");
        }

        if (!validateLowongan(logDTO.getIdLowongan())) {
            throw new ResourceNotFoundException("Lowongan tidak ditemukan");
        }

        if (!validateDosen(idDosen)) {
            throw new ResourceNotFoundException("Dosen tidak ditemukan");
        }

        // Validasi bahwa mahasiswa sudah diterima di lowongan ini
        if (!isMahasiswaAcceptedForLowongan(idMahasiswa, logDTO.getIdLowongan())) {
            throw new BadRequestException("Mahasiswa belum diterima pada lowongan ini");
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

    @Override
    public Log updateLogForMahasiswa(UUID id, LogDTO logDTO, UUID idMahasiswa) {
        Log log = findById(id);

        if (!log.getIdMahasiswa().equals(idMahasiswa)) {
            throw new BadRequestException("Anda tidak dapat mengubah log milik mahasiswa lain");
        }

        if (log.getStatus() != StatusLog.MENUNGGU) {
            throw new BadRequestException("Tidak dapat mengubah log yang sudah diverifikasi");
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
        Log log = findById(id);

        if (!log.getIdMahasiswa().equals(idMahasiswa)) {
            throw new BadRequestException("Anda tidak dapat menghapus log milik mahasiswa lain");
        }

        if (log.getStatus() != StatusLog.MENUNGGU) {
            throw new BadRequestException("Tidak dapat menghapus log yang sudah diverifikasi");
        }

        logRepository.delete(log);
    }

    @Override
    public double calculateHonor(UUID idMahasiswa, UUID idLowongan, int tahun, int bulan) {
        validateMahasiswa(idMahasiswa);
        validateLowongan(idLowongan);

        List<Log> acceptedLogs = logRepository.findAcceptedLogsByMahasiswaAndLowonganAndMonth(
                idMahasiswa, idLowongan, tahun, bulan);

        double totalHoursWorked = 0;
        for (Log log : acceptedLogs) {
            Duration duration = Duration.between(log.getWaktuMulai(), log.getWaktuSelesai());
            double hours = duration.toMinutes() / 60.0;
            totalHoursWorked += hours;
        }

        return totalHoursWorked * 27500;
    }

    // Periksa Log (Dosen)

    @Override
    public List<Log> findByDosen(UUID idDosen) {
        validateDosen(idDosen);
        return logRepository.findByIdDosen(idDosen);
    }

    @Override
    public Log verifyLog(UUID logId, StatusLog status, UUID idDosen) {
        Log log = findById(logId);

        if (!log.getIdDosen().equals(idDosen)) {
            throw new BadRequestException("Anda tidak dapat memverifikasi log mahasiswa yang bukan asisten anda");
        }

        if (status != StatusLog.DITERIMA && status != StatusLog.DITOLAK) {
            throw new BadRequestException("Status tidak valid. Gunakan DITERIMA atau DITOLAK.");
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
        validateLowongan(idLowongan);
        validateDosen(idDosen);

        if (!isDosenOwnsLowongan(idLowongan, idDosen)) {
            throw new BadRequestException("Dosen tidak dapat mengakses log untuk lowongan ini");
        }

        return logRepository.findByIdLowongan(idLowongan);
    }

    @Override
    public boolean validateLowongan(UUID idLowongan) {
        Lowongan lowongan = lowonganRepository.getLowonganById(idLowongan);
        return lowongan != null;
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
    public boolean isDosenOwnsLowongan(UUID idLowongan, UUID idDosen) {
        if (!validateDosen(idDosen)) {
            return false;
        }

        if (!validateLowongan(idLowongan)) {
            return false;
        }

        // Cek apakah idDosen terdaftar sebagai dosen di log untuk lowongan ini
        List<Log> logs = logRepository.findByIdLowongan(idLowongan);

        // Jika belum ada log untuk lowongan ini, anggap dosen dapat mengakses
        if (logs.isEmpty()) {
            return true;
        }

        // Jika sudah ada log, periksa apakah dosen adalah yang terdaftar
        return logs.stream().anyMatch(log -> log.getIdDosen().equals(idDosen));
    }

    private boolean isMahasiswaAcceptedForLowongan(UUID idMahasiswa, UUID idLowongan) {
        // Cek apakah mahasiswa sudah diterima di lowongan ini
        List<Lamaran> lamaranList = lamaranRepository.getLamaran();
        return lamaranList.stream()
                .anyMatch(lamaran ->
                        lamaran.getIdMahasiswa().equals(idMahasiswa) &&
                                lamaran.getIdLowongan().equals(idLowongan) &&
                                lamaran.getStatus() == id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran.DITERIMA
                );
    }
}