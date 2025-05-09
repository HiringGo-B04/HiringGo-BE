package id.ac.ui.cs.advprog.mendaftarlowongan.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.repository.LamaranRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LamaranServiceImpl implements LamaranService {

    @Autowired
    private LamaranRepository lamaranRepository;

    @Autowired
    private LowonganRepository lowonganClient;

    @Autowired
    private UserRepository userClient;

    // Constructor for testing purposes
    public LamaranServiceImpl(LamaranRepository lamaranRepository, LowonganRepository lowonganRepository, UserRepository userRepository) {
        this.lamaranRepository = lamaranRepository;
        this.lowonganClient = lowonganRepository;
        this.userClient = userRepository;
    }

    @Override
    public List<Lamaran> getLamaran() {
        return lamaranRepository.findAll();
    }

    @Override
    public Lamaran getLamaranById(UUID id) {
        return lamaranRepository.findById(id).orElse(null);
    }

    @Override
    public Lamaran createLamaran(Lamaran lamaran) {
        try {
            validateLamaran(lamaran);
        } catch (Exception e) {
            throw new RuntimeException("Error validating lamaran: " + e.getMessage());
        }
        return lamaranRepository.save(lamaran);
    }

    @Override
    public Lamaran updateLamaran(UUID id, Lamaran lamaran) {
        Lamaran existing = getLamaranById(id);
        if (existing == null) return null;

        existing.setIpk(lamaran.getIpk());
        existing.setSks(lamaran.getSks());
        existing.setStatus(lamaran.getStatus());
        existing.setIdMahasiswa(lamaran.getIdMahasiswa());
        existing.setIdLowongan(lamaran.getIdLowongan());

        return lamaranRepository.save(existing);
    }

    @Override
    public void deleteLamaran(UUID id) {
        lamaranRepository.deleteById(id);
    }

    @Override
    public boolean isLamaranExists(Lamaran lamaran) {
        return lamaranRepository.findAll().stream()
                .anyMatch(l -> l.getIdMahasiswa().equals(lamaran.getIdMahasiswa())
                        && l.getIdLowongan().equals(lamaran.getIdLowongan()));
    }

    @Override
    public void validateLamaran(Lamaran lamaran) throws Exception {

        boolean ipkValid = lamaran.getIpk() >= 0 && lamaran.getIpk() <= 4;
        boolean sksValid = lamaran.getSks() >= 0 && lamaran.getSks() <= 24;

        // Lowongan lowongan = lowonganClient.getLowonganById(lamaran.getIdLowongan());
        //  boolean lowonganExists = lowongan != null;
        boolean lowonganExists = true;

        // Optional<User> userOpt = userClient.findById(lamaran.getIdMahasiswa());
        // boolean isValidStudent = userOpt.isPresent() && "STUDENT".equalsIgnoreCase(userOpt.get().getRole());
        boolean isValidStudent = true;

        if (!ipkValid) {
            throw new Exception("IPK tidak valid");
        } else if (!sksValid) {
            throw new Exception("SKS tidak valid");
        } else if (!lowonganExists) {
            throw new Exception("Lowongan tidak ada");
        } else if (!isValidStudent) {
            throw new Exception("User tidak ditemukan atau bukan mahasiswa");
        }
    }

    @Override
    public List<Lamaran> getLamaranByLowonganId(UUID idLowongan) {
        return lamaranRepository.findAll().stream()
                .filter(l -> l.getIdLowongan().equals(idLowongan))
                .collect(Collectors.toList());
    }

    @Override
    public void acceptLamaran(UUID id) {
        Lamaran lamaran = getLamaranById(id);
        if (lamaran != null) {
            lamaran.setStatus(StatusLamaran.DITERIMA);
            lamaranRepository.save(lamaran);
        }
    }

    @Override
    public void rejectLamaran(UUID id) {
        Lamaran lamaran = getLamaranById(id);
        if (lamaran != null) {
            lamaran.setStatus(StatusLamaran.DITOLAK);
            lamaranRepository.save(lamaran);
        }
    }
}
