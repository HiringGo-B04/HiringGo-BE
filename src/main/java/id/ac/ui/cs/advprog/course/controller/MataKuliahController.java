package id.ac.ui.cs.advprog.course.controller;

import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.service.MataKuliahService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class MataKuliahController {

    private final MataKuliahService service;

    @GetMapping("/user/matakuliah")
    public CompletableFuture<ResponseEntity<List<MataKuliahDto>>> listAll() {
        return service.findAll()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/user/matakuliah/{kode}")
    public ResponseEntity<MataKuliahDto> getByKode(@PathVariable String kode) {
        var dto = service.findByKode(kode);
        return (dto == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    /* ---------- ADMIN CRUD ---------- */

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/matakuliah")
    public ResponseEntity<MataKuliahDto> create(@Valid @RequestBody MataKuliahDto dto) {
        var saved = service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{kode}").buildAndExpand(saved.kode()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/matakuliah/{kode}")
    public ResponseEntity<MataKuliahDto> replace(@PathVariable String kode,
                                                 @Valid @RequestBody MataKuliahDto dto) {
        return ResponseEntity.ok(service.update(kode, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/matakuliah/{kode}")
    public ResponseEntity<MataKuliahDto> patch(@PathVariable String kode,
                                               @Valid @RequestBody MataKuliahPatch patch) {
        return ResponseEntity.ok(service.partialUpdate(kode, patch));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/matakuliah/{kode}")
    public ResponseEntity<Void> delete(@PathVariable String kode) {
        service.delete(kode);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/matakuliah/{kode}/dosen/{userId}")
    public ResponseEntity<Void> addLecturer(@PathVariable String kode,
                                            @PathVariable UUID userId) {
        service.addLecturer(kode, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/matakuliah/{kode}/dosen/{userId}")
    public ResponseEntity<Void> removeLecturer(@PathVariable String kode,
                                               @PathVariable UUID userId) {
        service.removeLecturer(kode, userId);
        return ResponseEntity.noContent().build();
    }
}