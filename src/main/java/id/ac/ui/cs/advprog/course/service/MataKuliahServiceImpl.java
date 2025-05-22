package id.ac.ui.cs.advprog.course.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.mapper.MataKuliahMapper;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Primary
@Transactional
@RequiredArgsConstructor
public class MataKuliahServiceImpl implements MataKuliahService {

    private final MataKuliahRepository repo;
    private final MataKuliahMapper     mapper;
    private final UserRepository       userRepo;

    /* ============================================================
       READ
       ============================================================ */

    @Transactional(readOnly = true)
    @Override
    public Page<MataKuliahDto> findAll(Pageable pageable) {

        /*
         * Jika repository JPA mendukung paging native, gunakan langsung;
         * fallback → paging manual in-memory.
         */
        if (repo instanceof PagingAndSortingRepository<?,?> jpaRepo && pageable.isPaged()) {
            @SuppressWarnings("unchecked")
            Page<MataKuliah> page =
                    ((PagingAndSortingRepository<MataKuliah, ?>) jpaRepo).findAll(pageable);
            return page.map(mapper::toDto);
        }

        List<MataKuliah> all = repo.findAll();
        if (pageable.isUnpaged()) {
            return new PageImpl<>(all).map(mapper::toDto);
        }

        int start = (int) pageable.getOffset();
        int end   = Math.min(start + pageable.getPageSize(), all.size());
        return new PageImpl<>(all.subList(start, end), pageable, all.size())
                .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public MataKuliahDto findByKode(String kode) {
        return repo.findByKode(kode)
                .map(mapper::toDto)
                .orElse(null);
    }

    /* ============================================================
       CREATE
       ============================================================ */

    @Override
    public MataKuliahDto create(MataKuliahDto dto) {
        MataKuliah entity = mapper.toEntity(dto);

        /* Pastikan Set<User> tidak null */
        if (entity.getDosenPengampu() == null) {
            entity.setDosenPengampu(new HashSet<>());
        }

        MataKuliah saved = repo.addMataKuliah(entity);
        return mapper.toDto(saved);
    }

    /* ============================================================
       UPDATE  (PUT — replace seluruh kolom)
       ============================================================ */

    @Override
    public MataKuliahDto update(String kode, MataKuliahDto dto) {

        MataKuliah existing = repo.findByKode(kode)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Mata kuliah tidak ditemukan: " + kode));

        /* Salin field primitif */
        existing.setNama(dto.nama());
        existing.setSks(dto.sks());
        existing.setDeskripsi(dto.deskripsi());

        /* Perbarui relasi dosenPengampu */
        Set<User> newSet = new HashSet<>();
        if (dto.dosenPengampu() != null && !dto.dosenPengampu().isEmpty()) {
            newSet.addAll(userRepo.findAllById(dto.dosenPengampu()));
        }
        existing.setDosenPengampu(newSet);

        return mapper.toDto(repo.update(existing));
    }

    /* ============================================================
       PATCH  (partial update — hanya kolom non-null)
       ============================================================ */

    @Override
    public MataKuliahDto partialUpdate(String kode, MataKuliahPatch patch) {

        MataKuliah entity = repo.findByKode(kode)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Mata kuliah tidak ditemukan: " + kode));

        /* MapStruct akan meng-abaikan kolom null */
        mapper.patch(patch, entity);

        /*
         * Jika PATCH berisi daftar dosenPengampu,
         * tangani manual (karena Patch tidak di-map otomatis).
         */
        if (patch.dosenPengampu() != null) {
            Set<User> set = new HashSet<>(userRepo.findAllById(patch.dosenPengampu()));
            entity.setDosenPengampu(set);
        }

        return mapper.toDto(repo.update(entity));
    }

    /* ============================================================
       DELETE
       ============================================================ */

    @Override
    public void delete(String kode) {
        if (repo.findByKode(kode).isEmpty()) {
            throw new EntityNotFoundException("Mata kuliah tidak ditemukan: " + kode);
        }
        repo.deleteByKode(kode);
    }

    /* ============================================================
       LECTURER MANAGEMENT
       ============================================================ */

    @Override
    public void addLecturer(String kode, UUID userId) {

        MataKuliah mk = repo.findByKode(kode)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Mata kuliah tidak ditemukan: " + kode));

        User dosen = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Dosen tidak ditemukan: " + userId));

        mk.getDosenPengampu().add(dosen);
        repo.update(mk);
    }

    @Override
    public void removeLecturer(String kode, UUID userId) {
        MataKuliah mk = repo.findByKode(kode)
                .orElseThrow(() -> new EntityNotFoundException("Mata kuliah tidak ditemukan: " + kode));

        boolean removed = mk.getDosenPengampu()
                .removeIf(d -> d.getUserId().equals(userId));

        if (!removed) {                     // ← opsional, tapi baik untuk feedback
            throw new EntityNotFoundException("Dosen " + userId + " tidak terdaftar pada mata kuliah");
        }
        repo.update(mk);
    }
}
