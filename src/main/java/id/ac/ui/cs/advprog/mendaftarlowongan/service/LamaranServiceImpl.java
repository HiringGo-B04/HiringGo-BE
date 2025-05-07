package id.ac.ui.cs.advprog.mendaftarlowongan.service;

import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.repository.LamaranRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Profile("manual")
public class LamaranServiceImpl implements LamaranService {

    private final LamaranRepository lamaranRepository;
    private LowonganRepository lowonganClient = LowonganRepository.getInstance();

    @Autowired
    public LamaranServiceImpl(LamaranRepository lamaranRepository) {
        this.lamaranRepository = lamaranRepository;
    }

    // Constructor for testing purposes
    public LamaranServiceImpl(LamaranRepository lamaranRepository, LowonganRepository lowonganRepository) {
        this.lamaranRepository = lamaranRepository;
        this.lowonganClient = lowonganRepository;
    }

    @Override
    public List<Lamaran> getLamaran() {
        return lamaranRepository.getLamaran();
    }

    @Override
    public Lamaran getLamaranById(UUID id) {
        return lamaranRepository.getLamaranById(id);
    }

    @Override
    public Lamaran createLamaran(Lamaran lamaran) {
        if (!validateLamaran(lamaran)) {
            throw new IllegalArgumentException("SKS/IPK tidak valid atau Lowongan tidak ada");
        }
        return lamaranRepository.createLamaran(lamaran);
    }

    @Override
    public Lamaran updateLamaran(UUID id, Lamaran lamaran) {
        Lamaran existing = getLamaranById(id);
        if (existing == null) return null;

        existing.setIpk(lamaran.getIpk());
        existing.setSks(lamaran.getSks());
        existing.setStatus(lamaran.getStatus());
        return lamaranRepository.createLamaran(existing);
    }

    @Override
    public void deleteLamaran(UUID id) {
        lamaranRepository.deleteLamaran(id);
    }

    @Override
    public boolean isLamaranExists(Lamaran lamaran) {
        return lamaranRepository.getLamaran().stream()
                .anyMatch(l -> l.getIdMahasiswa().equals(lamaran.getIdMahasiswa())
                        && l.getIdLowongan().equals(lamaran.getIdLowongan()));
    }

    @Override
    public boolean validateLamaran(Lamaran lamaran) {

        boolean ipkValid = lamaran.getIpk() >= 0 && lamaran.getIpk() <= 4;
        boolean sksValid = lamaran.getSks() >= 0 && lamaran.getSks() <= 24;

        Lowongan lowongan = lowonganClient.getLowonganById(lamaran.getIdLowongan());
        boolean lowonganExists = lowongan != null;

        return ipkValid && sksValid && lowonganExists;
    }

    @Override
    public List<Lamaran> getLamaranByLowonganId(UUID idLowongan) {
        return lamaranRepository.getLamaran().stream()
                .filter(l -> l.getIdLowongan().equals(idLowongan))
                .collect(Collectors.toList());
    }

    @Override
    public void acceptLamaran(UUID id) {
        Lamaran lamaran = getLamaranById(id);
        if (lamaran != null) {
            lamaran.setStatus(StatusLamaran.DITERIMA);
            lamaranRepository.createLamaran(lamaran);
        }
    }

    @Override
    public void rejectLamaran(UUID id) {
        Lamaran lamaran = getLamaranById(id);
        if (lamaran != null) {
            lamaran.setStatus(StatusLamaran.DITOLAK);
            lamaranRepository.createLamaran(lamaran);
        }
    }
}
