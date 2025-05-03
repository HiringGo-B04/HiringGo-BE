package id.ac.ui.cs.advprog.course.service;

import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.mapper.MataKuliahMapper;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class MataKuliahServiceImpl implements MataKuliahService {

    private final MataKuliahRepository repo;
    private final MataKuliahMapper     mapper;

    public MataKuliahServiceImpl(MataKuliahRepository repo, MataKuliahMapper mapper) {
        this.repo   = repo;
        this.mapper = mapper;
    }

    /* ---------- CREATE ---------- */

    @Override
    public MataKuliahDto create(MataKuliahDto dto) {
        if (repo.findByKode(dto.kode()).isPresent())
            throw new RuntimeException("Kode sudah ada");

        MataKuliah saved = repo.save(mapper.toEntity(dto));
        return mapper.toDto(saved);
    }

    /* ---------- READ (paging) ---------- */

    @Override
    public Page<MataKuliahDto> findAll(Pageable pageable) {
        List<MataKuliahDto> dtoList = repo.findAll().stream()
                .map(mapper::toDto)
                .toList();
        // repository in‑memory → manual slice; untuk demo paging sederhana
        int start  = (int) pageable.getOffset();
        int end    = Math.min(start + pageable.getPageSize(), dtoList.size());
        List<MataKuliahDto> slice = dtoList.subList(start, end);

        return new PageImpl<>(slice, pageable, dtoList.size());
    }

    @Override
    public MataKuliahDto findByKode(String kode) {
        return repo.findByKode(kode)
                .map(mapper::toDto)
                .orElse(null);
    }

    /* ---------- FULL UPDATE ---------- */

    @Override
    public MataKuliahDto update(String kode, MataKuliahDto dto) {
        MataKuliah existing = repo.findByKode(kode)
                .orElseThrow(() -> new RuntimeException("Not found"));
        MataKuliah updated = mapper.toEntity(dto);
        updated.setKode(kode);
        repo.update(updated);
        return mapper.toDto(updated);
    }

    /* ---------- PARTIAL UPDATE ---------- */

    @Override
    public MataKuliahDto partialUpdate(String kode, MataKuliahPatch patch) {
        MataKuliah entity = repo.findByKode(kode)
                .orElseThrow(() -> new RuntimeException("Not found"));
        mapper.patch(patch, entity);          // MapStruct IGNORE null
        repo.update(entity);
        return mapper.toDto(entity);
    }

    /* ---------- DELETE ---------- */

    @Override
    public void delete(String kode) {
        if (repo.findByKode(kode).isEmpty())
            throw new RuntimeException("Not found");
        repo.deleteByKode(kode);
    }
}
