package id.ac.ui.cs.advprog.course.service;

import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.mapper.MataKuliahMapper;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageImpl;


@Service
@Primary
@Transactional
@RequiredArgsConstructor
public class MataKuliahServiceImpl implements MataKuliahService {

    private final MataKuliahRepository repo;
    private final MataKuliahMapper     mapper;
    private final UserRepository       userRepo;

    /* ---------- READ ---------- */

    @Transactional(readOnly = true)
    @Override
    public Page<MataKuliahDto> findAll(Pageable pageable) {

        /* INGAT NANTI DI TES DULU : Jika repo JPA mendukung paging langsung */
        if (repo instanceof org.springframework.data.repository.PagingAndSortingRepository<?,?> jpaRepo
                && pageable.isPaged()) {

            @SuppressWarnings("unchecked")
            Page<MataKuliah> jpaPage =
                    ((org.springframework.data.repository.PagingAndSortingRepository<MataKuliah, ?>) jpaRepo)
                            .findAll(pageable);

            return jpaPage.map(mapper::toDto);
        }

        List<MataKuliah> all = repo.findAll();
        if (pageable.isUnpaged()) {
            return new PageImpl<>(all).map(mapper::toDto);
        }

        int start = (int) pageable.getOffset();
        int end   = Math.min(start + pageable.getPageSize(), all.size());
        List<MataKuliah> slice = all.subList(start, end);

        return new PageImpl<>(slice, pageable, all.size()).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public MataKuliahDto findByKode(String kode) {
        return repo.findByKode(kode)
                .map(mapper::toDto)
                .orElse(null);
    }

    /* ---------- CREATE ---------- */

    @Override
    public MataKuliahDto create(MataKuliahDto dto) {
        MataKuliah entity = mapper.toEntity(dto);
        entity.setDosenPengampu(new java.util.HashSet<>());
        return mapper.toDto(repo.save(entity));
    }

    /* ---------- UPDATE (PUT) ---------- */

    @Override
    public MataKuliahDto update(String kode, MataKuliahDto dto) {

        repo.findByKode(kode)
                .orElseThrow(() -> new EntityNotFoundException("Mata kuliah tidak ditemukan: " + kode));

        MataKuliah entity = mapper.toEntity(dto);
        entity.setKode(kode);

        return mapper.toDto(repo.update(entity));
    }

    /* ---------- PATCH ---------- */

    @Override
    public MataKuliahDto partialUpdate(String kode, MataKuliahPatch patch) {
        MataKuliah entity = repo.findByKode(kode)
                .orElseThrow(() -> new EntityNotFoundException("Mata kuliah tidak ditemukan: " + kode));

        mapper.patch(patch, entity);
        return mapper.toDto(repo.update(entity));
    }

    /* ---------- DELETE ---------- */

    @Override
    public void delete(String kode) {
        if (repo.findByKode(kode).isEmpty()) {
            throw new EntityNotFoundException("Mata kuliah tidak ditemukan: " + kode);
        }
        repo.deleteByKode(kode);
    }

    @Override
    public void addLecturer(String kode, UUID userId) {
        MataKuliah mk = repo.findByKode(kode)
                .orElseThrow(() -> new EntityNotFoundException("Mata kuliah tidak ditemukan: " + kode));

        User dosen = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Dosen tidak ditemukan: " + userId));

        mk.getDosenPengampu().add(dosen);
        repo.update(mk);
    }

    @Override
    public void removeLecturer(String kode, UUID userId) {
        MataKuliah mk = repo.findByKode(kode)
                .orElseThrow(() -> new EntityNotFoundException("Mata kuliah tidak ditemukan: " + kode));

        mk.getDosenPengampu()
                .removeIf(d -> d.getUserId().equals(userId));

        repo.update(mk);
    }
}
