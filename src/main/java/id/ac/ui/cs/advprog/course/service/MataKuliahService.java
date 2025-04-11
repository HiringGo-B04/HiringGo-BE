package id.ac.ui.cs.advprog.course.service;

import id.ac.ui.cs.advprog.course.model.MataKuliah;

import java.util.List;

public interface MataKuliahService {

    void create(MataKuliah mk);

    List<MataKuliah> findAll();

    MataKuliah findByKode(String kode);

    void update(MataKuliah mk);

    void delete(String kode);
}


