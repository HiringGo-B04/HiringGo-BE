package id.ac.ui.cs.advprog.manajemenlowongan.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import id.ac.ui.cs.advprog.course.repository.JpaMataKuliahRepository;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.lang.Math.max;

@Service
public class LowonganServiceImpl implements LowonganService {

    private LowonganRepository lowonganRepository;
    private UserRepository userRepository;
    private JpaMataKuliahRepository mataKuliahRepository;

    public LowonganServiceImpl(UserRepository userRepository, LowonganRepository lowonganRepository, JpaMataKuliahRepository mataKuliahRepository) {
        this.userRepository = userRepository;
        this.lowonganRepository = lowonganRepository;
        this.mataKuliahRepository = mataKuliahRepository;
    }

    public boolean validateLowongan(Lowongan lowongan) {
        // Validasi data lowongan dasar
        if (lowongan.getMatkul() == null || lowongan.getMatkul().isEmpty() || !mataKuliahRepository.existsByKode(lowongan.getMatkul())) {
            throw new IllegalArgumentException("Nama Mata Kuliah tidak valid");
        }

        if (lowongan.getTerm() == null || lowongan.getTerm().isEmpty()) {
            throw new IllegalArgumentException("Semester tidak boleh kosong");
        }

        // Validasi semester harus "Genap" atau "Ganjil"
        if (!lowongan.getTerm().equals("Genap") && !lowongan.getTerm().equals("Ganjil")) {
            throw new IllegalArgumentException("Semester harus Genap atau Ganjil");
        }

        if (lowongan.getTahun() <= 0) {
            throw new IllegalArgumentException("Tahun ajaran tidak valid");
        }

        if (lowongan.getTotalAsdosNeeded() <= 0) {
            throw new IllegalArgumentException("Jumlah asisten dosen yang dibutuhkan harus lebih dari 0");
        }

        if (lowongan.getTahun() < 2025) {
            throw new IllegalArgumentException("Tahun ajaran harus lebih dari atau sama dengan 2025");
        }

        // Validasi kombinasi matakuliah, semester, dan tahun ajaran harus unik
        // Pemeriksaan ini sebaiknya dilakukan juga di isLowonganExists
        List<Lowongan> existingLowongan = lowonganRepository.findAll();
        boolean isDuplicate = existingLowongan.stream()
                .anyMatch(l -> !l.getId().equals(lowongan.getId()) && // Abaikan lowongan yang sedang diupdate
                        l.getMatkul().equals(lowongan.getMatkul()) &&
                        l.getTerm().equals(lowongan.getTerm()) &&
                        l.getTahun() == lowongan.getTahun());

        if (isDuplicate) {
            throw new IllegalArgumentException("Lowongan dengan kombinasi mata kuliah, semester, dan tahun ajaran yang sama sudah ada");
        }

        return true;
    }

    @Override
    public List<Lowongan> getLowongan() {
        return lowonganRepository.findAll();
    }

    @Override
    public Lowongan getLowonganById(UUID id) {
        return lowonganRepository.findById(id).orElse(null);
    }

    @Override
    public Lowongan addLowongan(Lowongan lowongan) {
        validateLowongan(lowongan);
        return lowonganRepository.save(lowongan);
    }

    @Transactional
    @Override
    public ResponseEntity<Map<String, Object>> updateLowongan(UUID id, Lowongan lowongan) {
        Map<String, Object> response = new HashMap<>();
        try {
            Lowongan existingLowongan = getLowonganById(id);
            if (existingLowongan == null) {
                response.put("message", "Lowongan dengan ID tersebut tidak ditemukan");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Update fields
            existingLowongan.setTerm(lowongan.getTerm());
            existingLowongan.setTahun(lowongan.getTahun());
            existingLowongan.setTotalAsdosNeeded(lowongan.getTotalAsdosNeeded());
            existingLowongan.setTotalAsdosAccepted(lowongan.getTotalAsdosAccepted());
            existingLowongan.setTotalAsdosRegistered(lowongan.getTotalAsdosRegistered());

            validateLowongan(existingLowongan);

            Lowongan updated = lowonganRepository.save(existingLowongan);

            response.put("message", "Lowongan berhasil diperbarui");
            response.put("data", updated);
            return ResponseEntity.ok(response);

        }  catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    public void deleteLowongan(UUID id) {
        lowonganRepository.deleteById(id);
    }

    public ResponseEntity<Map<String, Object>> getLowonganByDosen(UUID id){
        try {
            User user = userRepository.findByUserId(id);
            if (user == null || !user.getRole().equals("LECTURER")) {
                throw new IllegalArgumentException("Tidak ada dosen dengan id " + id);
            }

            List<Lowongan> lowongans = lowonganRepository.findAll();
            int teachingAssitant =  0;
            int needTeachingAssitant = 0;
            for (Lowongan lowongan : lowongans) {
                teachingAssitant += lowongan.getTotalAsdosAccepted();
                if(lowongan.getTotalAsdosNeeded() > lowongan.getTotalAsdosAccepted()){
                    needTeachingAssitant += 1;
                }
            }

            long totalCourse = mataKuliahRepository.findAll().stream()
                    .filter(mk -> mk.getDosenPengampu().stream()
                            .anyMatch(dosen -> dosen.getUserId().equals(id)))
                    .count();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Success");
            response.put("course", totalCourse);
            response.put("assistant", teachingAssitant);
            response.put("vacan", needTeachingAssitant);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Gagal mengambil lowongan: " + e.getMessage());
            errorResponse.put("data", new ArrayList<>());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Override
    public boolean isLowonganExists(Lowongan lowongan) {
        List<Lowongan> existingLowongans = lowonganRepository.findAll();

        for (Lowongan existing : existingLowongans) {
            if (isSameLowongan(existing, lowongan)) {
                return true;
            }
        }

        return false;
    }

    private boolean isSameLowongan(Lowongan a, Lowongan b) {
        return a.getMatkul().equals(b.getMatkul()) &&
                a.getTahun() == b.getTahun() &&
                a.getTerm().equals(b.getTerm());
    }
}