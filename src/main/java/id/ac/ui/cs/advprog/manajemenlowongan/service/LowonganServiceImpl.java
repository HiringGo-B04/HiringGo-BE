package id.ac.ui.cs.advprog.manajemenlowongan.service;

import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LowonganServiceImpl implements LowonganService {
    
    private final LowonganRepository lowonganRepository;

    public LowonganServiceImpl(LowonganRepository lowonganRepository) {
        this.lowonganRepository = lowonganRepository;
    }

    @Override
    public List<Lowongan> getLowongan() {
        return null;
    }

    @Override
    public Lowongan getLowonganById(UUID id) {
        return null;
    }

    @Override
    public Lowongan addLowongan(Lowongan lowongan) {
        return null;
    }

    @Override
    public Lowongan updateLowongan(UUID id, Lowongan lowongan) {
        return null;
    }

    public void deleteLowongan(UUID id) {}

    @Override
    public boolean isLowonganExists(Lowongan lowongan) {
        return false;
    }

    @Override
    public boolean isLowonganClosed(Lowongan lowongan) {
        return false;
    }
}