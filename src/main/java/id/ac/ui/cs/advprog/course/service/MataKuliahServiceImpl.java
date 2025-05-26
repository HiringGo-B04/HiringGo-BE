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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Primary
@Transactional
@RequiredArgsConstructor
public class MataKuliahServiceImpl implements MataKuliahService {

    private final MataKuliahRepository repo;
    private final MataKuliahMapper     mapper;
    private final UserRepository       userRepo;

    @Async
    @Transactional(readOnly = true)
    @Override
    public CompletableFuture<List<MataKuliahDto>> findAll() {
        return CompletableFuture.supplyAsync(() -> {
            List<MataKuliah> all = repo.findAll();
            return all.stream()
                    .map(mapper::toDto)
                    .toList();
        });
    }

    @Transactional(readOnly = true)
    @Override
    public MataKuliahDto findByKode(String kode) {
        return repo.findByKode(kode)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public MataKuliahDto create(MataKuliahDto dto) {
        MataKuliah entity = mapper.toEntity(dto);
        if (entity.getDosenPengampu() == null) {
            entity.setDosenPengampu(new HashSet<>());
        }

        MataKuliah saved = repo.addMataKuliah(entity);
        return mapper.toDto(saved);
    }

    @Override
    public MataKuliahDto update(String kode, MataKuliahDto dto) {
        MataKuliah existing = repo.findByKode(kode)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Mata kuliah tidak ditemukan: " + kode));
        existing.setNama(dto.nama());
        existing.setSks(dto.sks());
        existing.setDeskripsi(dto.deskripsi());
        Set<User> newSet = new HashSet<>();
        if (dto.dosenPengampu() != null && !dto.dosenPengampu().isEmpty()) {
            newSet.addAll(userRepo.findAllById(dto.dosenPengampu()));
        }
        existing.setDosenPengampu(newSet);

        return mapper.toDto(repo.update(existing));
    }

    @Override
    public MataKuliahDto partialUpdate(String kode, MataKuliahPatch patch) {

        MataKuliah entity = repo.findByKode(kode)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Mata kuliah tidak ditemukan: " + kode));
        mapper.patch(patch, entity);
        if (patch.dosenPengampu() != null) {
            Set<User> set = new HashSet<>(userRepo.findAllById(patch.dosenPengampu()));
            entity.setDosenPengampu(set);
        }

        return mapper.toDto(repo.update(entity));
    }

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

        if (!removed) {
            throw new EntityNotFoundException("Dosen " + userId + " tidak terdaftar pada mata kuliah");
        }
        repo.update(mk);
    }
}