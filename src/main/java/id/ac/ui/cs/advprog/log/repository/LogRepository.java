package id.ac.ui.cs.advprog.log.repository;

import id.ac.ui.cs.advprog.log.model.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class LogRepository {

    private final List<Log> daftarLog  = new ArrayList<>();

    public List<Log> findAll() {
        return new ArrayList<>(daftarLog);
    }

    public Log findById(UUID id) {
        return daftarLog.stream()
                .filter(log -> log.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Log save(Log log) {
        if (log.getId() == null) {
            log.setId(UUID.randomUUID());
        }
        daftarLog.add(log);
        return log;
    }

    public Log update(UUID id, Log updatedLog) {
        Log existingLog = findById(id);
        if (existingLog != null) {
            existingLog.setJudul(updatedLog.getJudul());
            existingLog.setKeterangan(updatedLog.getKeterangan());
            existingLog.setKategori(updatedLog.getKategori());
            existingLog.setTanggalLog(updatedLog.getTanggalLog());
            existingLog.setWaktuMulai(updatedLog.getWaktuMulai());
            existingLog.setWaktuSelesai(updatedLog.getWaktuSelesai());
            existingLog.setPesanUntukDosen(updatedLog.getPesanUntukDosen());
            existingLog.setStatus(updatedLog.getStatus());
        }
        return existingLog;
    }

    public void deleteById(UUID id) {
        daftarLog.removeIf(log -> log.getId().equals(id));
    }

    public void clearAll() {
        daftarLog.clear();
    }
}

