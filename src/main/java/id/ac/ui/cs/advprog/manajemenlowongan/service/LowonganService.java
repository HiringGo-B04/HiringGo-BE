package id.ac.ui.cs.advprog.manajemenlowongan.service;

import id.ac.ui.cs.advprog.manajemenlowongan.model.*;

import java.util.List;
import java.util.UUID;

public interface LowonganService {
    public List<Lowongan> findAll();
    public Lowongan addLowongan(Lowongan lowongan);
    public Lowongan updateLowongan(Lowongan lowongan);
    public Lowongan deleteLowongan(Lowongan lowongan);
    public Lowongan findLowonganById(UUID id);
}
