package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;

import java.util.List;
import java.util.Optional;

public interface MataKuliahRepository {

    MataKuliah save(MataKuliah mk);

    MataKuliah update(MataKuliah mk);

    List<MataKuliah>        findAll();

    Optional<MataKuliah>    findByKode(String kode);

    void deleteByKode(String kode);
}
