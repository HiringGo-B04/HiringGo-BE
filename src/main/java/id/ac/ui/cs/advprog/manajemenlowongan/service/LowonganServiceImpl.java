package id.ac.ui.cs.advprog.manajemenlowongan.service;

import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Profile("manual")
public class LowonganServiceImpl implements LowonganService {
    
    private final LowonganRepository lowonganRepository;

    public boolean validateLowongan(Lowongan lowongan) {
        // Validasi data lowongan dasar
        if (lowongan.getMatkul() == null || lowongan.getMatkul().isEmpty()) {
            throw new IllegalArgumentException("Mata kuliah tidak boleh kosong");
        }

        if (lowongan.getTerm() == null || lowongan.getTerm().isEmpty()) {
            throw new IllegalArgumentException("Semester tidak boleh kosong");
        }

        // Validasi semester harus "Genap" atau "Ganjil"
        if (!lowongan.getTerm().equals("Genap") && !lowongan.getTerm().equals("Ganjil")) {
            throw new IllegalArgumentException("Semester harus Genap atau Ganjil");
        }

        if (lowongan.getYear() <= 0) {
            throw new IllegalArgumentException("Tahun ajaran tidak valid");
        }

        if (lowongan.getTotalAsdosNeeded() <= 0) {
            throw new IllegalArgumentException("Jumlah asisten dosen yang dibutuhkan harus lebih dari 0");
        }

        // Validasi kombinasi matakuliah, semester, dan tahun ajaran harus unik
        // Pemeriksaan ini sebaiknya dilakukan juga di isLowonganExists
        List<Lowongan> existingLowongan = lowonganRepository.getLowongan();
        boolean isDuplicate = existingLowongan.stream()
                .anyMatch(l -> !l.getId().equals(lowongan.getId()) && // Abaikan lowongan yang sedang diupdate
                        l.getMatkul().equals(lowongan.getMatkul()) &&
                        l.getTerm().equals(lowongan.getTerm()) &&
                        l.getYear() == lowongan.getYear());

        if (isDuplicate) {
            throw new IllegalArgumentException("Lowongan dengan kombinasi mata kuliah, semester, dan tahun ajaran yang sama sudah ada");
        }

        return true;
    }

    public LowonganServiceImpl(LowonganRepository lowonganRepository) {
        this.lowonganRepository = lowonganRepository;
    }

    @Override
    public List<Lowongan> getLowongan() {
        return lowonganRepository.getLowongan();
    }

    @Override
    public Lowongan getLowonganById(UUID id) {
        return lowonganRepository.getLowonganById(id);
    }

    @Override
    public Lowongan addLowongan(Lowongan lowongan) {
        if (lowongan.getYear() < 2025) {
            throw new IllegalArgumentException("Tahun ajaran harus lebih dari atau sama dengan 2025");
        }
        validateLowongan(lowongan);
        return lowonganRepository.addLowongan(lowongan);
    }

    @Override
    public Lowongan updateLowongan(UUID id, Lowongan lowongan) {
        Lowongan existingLowongan = lowonganRepository.getLowonganById(id);
        if (existingLowongan == null) {
            throw new IllegalArgumentException("Lowongan dengan ID tersebut tidak ditemukan");
        }

        // Pastikan ID lowongan yang diupdate sama dengan ID yang diminta
        lowongan.setId(id);

        // validateLowongan sekarang melempar exception langsung dengan pesan spesifik
        validateLowongan(lowongan);

        return lowonganRepository.updateLowongan(id, lowongan);
    }

    public void deleteLowongan(UUID id) {
        lowonganRepository.deleteLowongan(id);
    }

    @Override
    public boolean isLowonganExists(Lowongan lowongan) {
        List<Lowongan> existingLowongans = lowonganRepository.getLowongan();

        for (Lowongan existing : existingLowongans) {
            if (isSameLowongan(existing, lowongan)) {
                return true;
            }
        }

        return false;
    }

    private boolean isSameLowongan(Lowongan a, Lowongan b) {
        return a.getMatkul().equals(b.getMatkul()) &&
                a.getYear() == b.getYear() &&
                a.getTerm().equals(b.getTerm());
    }

    @Override
    public boolean isLowonganClosed(Lowongan lowongan) {
        // Lowongan dianggap tutup jika jumlah asisten dosen yang diterima
        // sudah sama dengan atau melebihi jumlah yang dibutuhkan
        if (lowongan.getTotalAsdosAccepted() >= lowongan.getTotalAsdosNeeded()) {
            return true;
        }

        return false;
    }
}