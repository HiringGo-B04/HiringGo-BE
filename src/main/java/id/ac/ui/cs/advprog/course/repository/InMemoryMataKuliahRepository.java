package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

/** CATATAN
 * Implementasi repository in‑memory – dipakai pada profile <b>"test"</b>
 * agar unit‑test tidak membutuhkan database eksternal.
 * <p>
 *   Penyimpanan sederhana menggunakan {@link HashMap} (thread‑unsafe,
 *   sudah memadai untuk skenario single‑threaded test).
 */
@Repository
@Profile("test")
public class InMemoryMataKuliahRepository implements MataKuliahRepository {

    private final Map<String, MataKuliah> storage = new HashMap<>();

    /* ---------- CREATE ---------- */

    @Override
    public MataKuliah save(MataKuliah mk) {
        if (storage.containsKey(mk.getKode())) {
            throw new RuntimeException("Kode mata kuliah sudah terdaftar: " + mk.getKode());
        }
        storage.put(mk.getKode(), mk);
        return mk;
    }

    /* ---------- UPDATE ---------- */

    @Override
    public MataKuliah update(MataKuliah mk) {
        if (!storage.containsKey(mk.getKode())) {
            throw new RuntimeException("Mata kuliah tidak ditemukan: " + mk.getKode());
        }
        storage.put(mk.getKode(), mk);
        return mk;
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

    /* ---------- DELETE ---------- */

    @Override
    public void deleteByKode(String kode) {
        storage.remove(kode);
    }
}
