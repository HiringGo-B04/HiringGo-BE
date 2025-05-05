package id.ac.ui.cs.advprog.manajemenlowongan.repository;

import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.UUID;

@Repository
public class LowonganRepository {
    private static LowonganRepository instance;
    private List<Lowongan> daftarLowongan;

    private LowonganRepository() {
        this.daftarLowongan = new ArrayList<>();
    }

    public static LowonganRepository getInstance() {
        if (instance == null) {
            instance = new LowonganRepository();
        }
        return instance;
    }

    public List<Lowongan> getLowongan() {
        return daftarLowongan;
    }

    public Lowongan getLowonganById(UUID id) {
        return daftarLowongan.stream()
                .filter(x -> x.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Lowongan addLowongan(Lowongan lowongan) {
        daftarLowongan.add(lowongan);
        return lowongan;
    }

    public Lowongan updateLowongan(UUID id, Lowongan updatedLowongan) {
        Lowongan existingLowongan = getLowonganById(id);
        if (existingLowongan != null) {
            existingLowongan.setTotalAsdosNeeded(updatedLowongan.getTotalAsdosNeeded());
            existingLowongan.setTotalAsdosRegistered(updatedLowongan.getTotalAsdosRegistered());
            existingLowongan.setTotalAsdosAccepted(updatedLowongan.getTotalAsdosAccepted());
        }
        return existingLowongan;
    }

    public void deleteLowongan(UUID id) {
        daftarLowongan.removeIf(x -> x.getId().equals(id));
    }
}
