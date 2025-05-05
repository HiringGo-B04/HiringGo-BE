package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Primary                   // ← agar Spring memilih repo ini
public class InMemoryMataKuliahRepository implements MataKuliahRepository {

    private final Map<String, MataKuliah> storage = new HashMap<>();

    /* ---------- CREATE ---------- */
    @Override
    public MataKuliah save(MataKuliah mk) {
        if (storage.containsKey(mk.getKode())) {
            throw new RuntimeException("Kode mata kuliah sudah terdaftar: " + mk.getKode());
        }
        storage.put(mk.getKode(), mk);
        return mk;                    // ← return entity
    }

    /* ---------- READ ---------- */
    @Override
    public List<MataKuliah> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<MataKuliah> findByKode(String kode) {
        return Optional.ofNullable(storage.get(kode));
    }

    /* ---------- UPDATE ---------- */
    @Override
    public MataKuliah update(MataKuliah mk) {
        if (!storage.containsKey(mk.getKode())) {
            throw new RuntimeException("Mata kuliah tidak ditemukan: " + mk.getKode());
        }
        storage.put(mk.getKode(), mk);
        return mk;                    // ← return entity
    }

    /* ---------- DELETE ---------- */
    @Override
    public void deleteByKode(String kode) {
        storage.remove(kode);
    }
}
