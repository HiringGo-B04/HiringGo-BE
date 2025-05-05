package id.ac.ui.cs.advprog.mendaftarlowongan.repository;

import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class LamaranRepository {
    private static LamaranRepository instance;
    private List<Lamaran> daftarLamaran;

    private LamaranRepository() {
        this.daftarLamaran = new ArrayList<>();
    }

    public static LamaranRepository getInstance() {
        if (instance == null) {
            instance = new LamaranRepository();
        }
        return instance;
    }

    public List<Lamaran> getLamaran() {
        return daftarLamaran;
    }

    public Lamaran getLamaranById(UUID id) {
        return daftarLamaran.stream()
                .filter(lamaran -> lamaran.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Lamaran createLamaran(Lamaran lamaran) {
        daftarLamaran.add(lamaran);
        return lamaran;
    }

    public Lamaran updateLamaran(UUID id, Lamaran updatedLamaran) {
        Lamaran existingLamaran = getLamaranById(id);
        if (existingLamaran != null) {
            existingLamaran.setSks(updatedLamaran.getSks());
            existingLamaran.setIpk(updatedLamaran.getIpk());
            existingLamaran.setStatus(updatedLamaran.getStatus());
            existingLamaran.setIdMahasiswa(updatedLamaran.getIdMahasiswa());
            existingLamaran.setIdLowongan(updatedLamaran.getIdLowongan());
        }
        return existingLamaran;
    }

    public void deleteLamaran(UUID id) {
        daftarLamaran.removeIf(lamaran -> lamaran.getId().equals(id));
    }
}