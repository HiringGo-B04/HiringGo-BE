package id.ac.ui.cs.advprog.manajemenlowongan.repository;

import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lamaran;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.UUID;

@Repository
public class LowonganRepository {
    private List<Lowongan> lowonganData = new ArrayList<>();

    public Iterator<Lowongan> findAll() {
        return lowonganData.iterator();
    }

    public Lowongan getLowonganById(UUID id) {
        return lowonganData.stream()
                .filter(x -> x.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Lowongan addLowongan(Lowongan lowongan) {
        if (lowongan.getId() == null) {
            UUID uuid = UUID.randomUUID();
            lowongan.setId(uuid);
        }
        lowonganData.add(lowongan);
        return lowongan;
    }

    public Lowongan updateLowongan(UUID id, Lowongan lowongan) {
        for (int i = 0; i < lowonganData.size(); i++) {
            if (lowonganData.get(i).getId().equals(id)) {
                lowongan.setId(id);
                lowonganData.set(i, lowongan);
                return lowongan;
            }
        }
        return null; // Return null apabila tidak ditemukan lowongannya
    }

    public Lowongan deleteLowongan(UUID id) {
        Iterator<Lowongan> iterator = lowonganData.iterator();
        while (iterator.hasNext()) {
            Lowongan lowongan = iterator.next();
            if (lowongan.getId().equals(id)) {
                iterator.remove();
                return lowongan; // Kembalikan lowongan yang dihapus
            }
        }
        return null; // Jika tidak ditemukan
    }

    public Lamaran acceptLamaran(UUID idLamaran) {
        return null;
    }

    public Lamaran rejectLamaran(UUID idLamaran) {
        return null;
    }
}
