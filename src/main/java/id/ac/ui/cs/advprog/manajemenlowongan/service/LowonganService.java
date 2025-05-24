package id.ac.ui.cs.advprog.manajemenlowongan.service;

import id.ac.ui.cs.advprog.manajemenlowongan.model.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LowonganService {
    List<Lowongan> getLowongan();
    Lowongan getLowonganById(UUID id);
    Lowongan addLowongan(Lowongan lowongan);
    Lowongan updateLowongan(UUID id, Lowongan lowongan);
    void deleteLowongan(UUID id);
    boolean isLowonganExists(Lowongan lowongan);
    ResponseEntity<Map<String, Object>> getLowonganByDosen(UUID id);
}
