package id.ac.ui.cs.advprog.mendaftarlowongan.service;

import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.repository.LamaranRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LamaranServiceImpl implements LamaranService {

    private final LamaranRepository lamaranRepository;

    public LamaranServiceImpl(LamaranRepository lamaranRepository) {
        this.lamaranRepository = lamaranRepository;
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
            throw new IllegalArgumentException("SKS/IPK tidak valid");
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
        return lamaran.getIpk() >= 0 && lamaran.getIpk() <= 4 && lamaran.getSks() >= 0;
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
