package id.ac.ui.cs.advprog.course.service;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MataKuliahServiceImpl implements MataKuliahService {

    private final MataKuliahRepository repository;

    public MataKuliahServiceImpl(MataKuliahRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(MataKuliah mk) {
        if (mk.getKode() == null || mk.getKode().trim().isEmpty()) {
            throw new RuntimeException("Kode mata kuliah tidak boleh kosong");
        }
        if (mk.getSks() < 0) {
            throw new RuntimeException("SKS tidak boleh negatif");
        }
        if (repository.findByKode(mk.getKode()).isPresent()) {
            throw new RuntimeException("Kode mata kuliah sudah terdaftar");
        }
        repository.save(mk);
    }

    @Override
    public List<MataKuliah> findAll() {
        return repository.findAll();
    }

    @Override
    public MataKuliah findByKode(String kode) {
        return repository.findByKode(kode).orElse(null);
    }

    @Override
    public void update(MataKuliah mk) {
        if (repository.findByKode(mk.getKode()).isEmpty()) {
            throw new RuntimeException("Mata kuliah tidak ditemukan");
        }
        repository.update(mk);
    }

    @Override
    public void delete(String kode) {
        if (repository.findByKode(kode).isEmpty()) {
            throw new RuntimeException("Mata kuliah tidak ditemukan");
        }
        repository.deleteByKode(kode);
    }
}


