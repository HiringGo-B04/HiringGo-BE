package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;

import java.util.List;
import java.util.Optional;

public interface MataKuliahRepository {

    /* ---------- CREATE / UPDATE ---------- */

    MataKuliah addMataKuliah(MataKuliah mk);

    default MataKuliah update(MataKuliah mk) {
        return addMataKuliah(mk);
    }

    /* ---------- READ ---------- */

    List<MataKuliah> findAll();
    Optional<MataKuliah> findByKode(String kode);

    /* ---------- DELETE ---------- */

    void deleteByKode(String kode);
}