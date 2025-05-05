package id.ac.ui.cs.advprog.course.service;

import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MataKuliahService {

    MataKuliahDto create(MataKuliahDto dto);

    Page<MataKuliahDto> findAll(Pageable pageable);

    MataKuliahDto findByKode(String kode);

    MataKuliahDto update(String kode, MataKuliahDto dto);

    /** partial update untuk HTTP PATCH */
    MataKuliahDto partialUpdate(String kode, MataKuliahPatch patch);

    void delete(String kode);
}
