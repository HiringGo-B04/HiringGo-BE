package id.ac.ui.cs.advprog.course.service;

import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface MataKuliahService {
    Page<MataKuliahDto> findAll(Pageable pageable);
    MataKuliahDto findByKode(String kode);
    MataKuliahDto create(MataKuliahDto dto);
    MataKuliahDto update(String kode, MataKuliahDto dto);
    MataKuliahDto partialUpdate(String kode, MataKuliahPatch patch);
    void delete(String kode);
    void addLecturer(String kodeMatkul, UUID userId);
    void removeLecturer(String kodeMatkul, UUID userId);
}
