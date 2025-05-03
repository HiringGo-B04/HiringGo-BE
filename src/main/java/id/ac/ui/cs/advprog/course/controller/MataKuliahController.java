package id.ac.ui.cs.advprog.course.controller;

import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.service.MataKuliahService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/matakuliah")
@PreAuthorize("hasRole('ADMIN')")
public class MataKuliahController {

    private final MataKuliahService service;

    public MataKuliahController(MataKuliahService service) {
        this.service = service;
    }

    /* ---------- READ ---------- */

    @GetMapping
    public ResponseEntity<Page<MataKuliahDto>> list(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{kode}")
    public ResponseEntity<MataKuliahDto> get(@PathVariable String kode) {
        MataKuliahDto dto = service.findByKode(kode);
        return (dto == null) ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(dto);
    }

    /* ---------- CREATE ---------- */

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MataKuliahDto dto) {
        try {
            MataKuliahDto saved = service.create(dto);
            URI loc = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{kode}").buildAndExpand(saved.kode()).toUri();
            return ResponseEntity.created(loc).body(saved);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /* ---------- FULL UPDATE ---------- */

    @PutMapping("/{kode}")
    public ResponseEntity<?> replace(@PathVariable String kode,
                                     @Valid @RequestBody MataKuliahDto dto) {
        try {
            MataKuliahDto updated = service.update(kode, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /* ---------- PARTIAL UPDATE ---------- */

    @PatchMapping("/{kode}")
    public ResponseEntity<?> patch(@PathVariable String kode,
                                   @Valid @RequestBody MataKuliahPatch patch) {
        try {
            MataKuliahDto patched = service.partialUpdate(kode, patch);
            return ResponseEntity.ok(patched);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /* ---------- DELETE ---------- */

    @DeleteMapping("/{kode}")
    public ResponseEntity<?> delete(@PathVariable String kode) {
        try {
            service.delete(kode);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
