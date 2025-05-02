package id.ac.ui.cs.advprog.log.service;

import id.ac.ui.cs.advprog.log.model.Log;

import java.util.List;
import java.util.UUID;

public interface LogService {
    Log create(Log log);
    List<Log> findAll();
    Log findById(UUID id);
    Log update(UUID id, Log updatedLog);
    void delete(UUID id);
}
