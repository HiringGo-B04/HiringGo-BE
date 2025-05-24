package id.ac.ui.cs.advprog.course.service;

import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MataKuliahService {
    CompletableFuture<List<MataKuliahDto>> findAll();
    MataKuliahDto findByKode(String kode);
    MataKuliahDto create(MataKuliahDto dto);
    MataKuliahDto update(String kode, MataKuliahDto dto);
    MataKuliahDto partialUpdate(String kode, MataKuliahPatch patch);
    void delete(String kode);
    void addLecturer(String kodeMatkul, UUID userId);
    void removeLecturer(String kodeMatkul, UUID userId);
}