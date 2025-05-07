package id.ac.ui.cs.advprog.log.service;

import id.ac.ui.cs.advprog.log.model.Log;
import id.ac.ui.cs.advprog.log.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Profile("manual")
public class LogServiceImpl implements LogService {

    @Autowired
    private LogRepository logRepository;

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
        return logRepository.findById(id);
    }

    @Override
    public Log update(UUID id, Log updatedLog) {
        return logRepository.update(id, updatedLog);
    }

    @Override
    public void delete(UUID id) {
        logRepository.deleteById(id);
    }
}
