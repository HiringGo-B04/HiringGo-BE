package id.ac.ui.cs.advprog.log.repository;

import id.ac.ui.cs.advprog.log.model.Log;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class LogRepository {

    private static LogRepository instance;
    private final List<Log> daftarLog;

    private LogRepository() {
        this.daftarLog = new ArrayList<>();
    }

    public static LogRepository getInstance() {
    }

    public List<Log> findAll() {
    }

    public Log findById(UUID id) {
    }

    public Log save(Log log) {
    }

    public Log update(UUID id, Log updatedLog) {
        return existingLog;
    }

    public void deleteById(UUID id) {
    }

    public void clearAll() {
        daftarLog.clear();
    }
}

