package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;

import java.util.*;

/**
 * Implementasi in-memory dari MataKuliahRepository.
 * Menyimpan data MataKuliah dalam sebuah Map<String, MataKuliah> (key: kode)
 */
public class InMemoryMataKuliahRepository implements MataKuliahRepository {

    /**
     * Penyimpanan utama in-memory:
     * key   = kode unik mata kuliah
     * value = objek MataKuliah
     */
    private final Map<String, MataKuliah> storage = new HashMap<>();

    @Override
    public void save(MataKuliah mk) {
        if (storage.containsKey(mk.getKode())) {
            throw new RuntimeException("Kode mata kuliah sudah terdaftar: " + mk.getKode());
        }
        storage.put(mk.getKode(), mk);
    }

    @Override
    public List<MataKuliah> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<MataKuliah> findByKode(String kode) {
        MataKuliah result = storage.get(kode);
        return Optional.ofNullable(result);
    }

    @Override
    public void update(MataKuliah mk) {
        // Cek apakah data lama ada
        if (!storage.containsKey(mk.getKode())) {
            throw new RuntimeException("Mata kuliah tidak ditemukan: " + mk.getKode());
        }
        storage.put(mk.getKode(), mk);
    }

    @Override
    public void deleteByKode(String kode) {
        storage.remove(kode);
    }
}

