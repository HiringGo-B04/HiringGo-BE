package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.course.model.MataKuliah;

import java.util.List;
import java.util.Optional;

public interface MataKuliahRepository {

    MataKuliah addMataKuliah(MataKuliah mk);
    default MataKuliah update(MataKuliah mk) {
        return addMataKuliah(mk);
    }
    List<MataKuliah> findAll();
    Optional<MataKuliah> findByKode(String kode);
    void deleteByKode(String kode);
    List<MataKuliah> findByDosenPengampuContaining(User dosen);
}