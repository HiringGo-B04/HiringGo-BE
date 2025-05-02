package id.ac.ui.cs.advprog.course.controller;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import id.ac.ui.cs.advprog.course.service.MataKuliahService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Controller Manajemen Mata Kuliah – API v1
 *
 * Endpoint:
 *  • GET    /api/v1/matakuliah            → daftar mata kuliah
 *  • GET    /api/v1/matakuliah/{kode}    → detail
 *  • POST   /api/v1/matakuliah            → buat (201 + Location)
 *  • PUT    /api/v1/matakuliah/{kode}    → ganti seluruh data
 *  • PATCH  /api/v1/matakuliah/{kode}    → ubah sebagian field
 *  • DELETE /api/v1/matakuliah/{kode}    → hapus
 *
 * Semua endpoint dibatasi role ADMIN via @PreAuthorize.
 * ResponseEntity dipakai agar status HTTP & header dapat diatur tepat.
 */
@RestController
@RequestMapping("/api/v1/matakuliah")
@PreAuthorize("hasRole('ADMIN')")
public class MataKuliahController {

    private final MataKuliahService mataKuliahService;

    public MataKuliahController(MataKuliahService mataKuliahService) {
        this.mataKuliahService = mataKuliahService;
    }

    /* ---------- READ ---------- */

    @GetMapping
    public ResponseEntity<List<MataKuliah>> getAllMataKuliah() {
        return ResponseEntity.ok(mataKuliahService.findAll());
    }

    @GetMapping("/{kode}")
    public ResponseEntity<MataKuliah> getMataKuliahByKode(@PathVariable String kode) {
        MataKuliah mk = mataKuliahService.findByKode(kode);
        return (mk == null) ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(mk);
    }

    /* ---------- CREATE ---------- */

    @PostMapping
    public ResponseEntity<?> createMataKuliah(@Valid @RequestBody MataKuliah mk) {
        try {
            mataKuliahService.create(mk);   // bisa lempar duplikat
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{kode}")
                    .buildAndExpand(mk.getKode()).toUri();
            return ResponseEntity.created(location).body(mk);   // 201 Created
        } catch (RuntimeException ex) {
            // duplikat kode → 400 Bad Request + pesan
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /* ---------- FULL UPDATE ---------- */

    @PutMapping("/{kode}")
    public ResponseEntity<?> updateMataKuliah(@PathVariable String kode,
                                              @Valid @RequestBody MataKuliah mk) {
        try {
            mk.setKode(kode);
            mataKuliahService.update(mk);
            return ResponseEntity.ok(mk);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /* ---------- PARTIAL UPDATE ---------- */

    @PatchMapping("/{kode}")
    public ResponseEntity<?> patchMataKuliah(@PathVariable String kode,
                                             @RequestBody Map<String, Object> updates) {
        try {
            MataKuliah existing = mataKuliahService.findByKode(kode);
            if (existing == null) return ResponseEntity.notFound().build();

            if (updates.containsKey("sks"))
                existing.setSks((Integer) updates.get("sks"));
            if (updates.containsKey("deskripsi"))
                existing.setDeskripsi((String) updates.get("deskripsi"));

            mataKuliahService.update(existing);
            return ResponseEntity.ok(existing);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /* ---------- DELETE ---------- */

    @DeleteMapping("/{kode}")
    public ResponseEntity<?> deleteMataKuliah(@PathVariable String kode) {
        try {
            mataKuliahService.delete(kode);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {     // not‑found
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
