package id.ac.ui.cs.advprog.course.controller;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import id.ac.ui.cs.advprog.course.service.MataKuliahService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller untuk Manajemen Mata Kuliah, menyediakan endpoint CRUD:
 * 1. GET    /api/matakuliah          -> Menampilkan semua data mata kuliah
 * 2. GET    /api/matakuliah/{kode}   -> Menampilkan data mata kuliah tertentu
 * 3. POST   /api/matakuliah          -> Membuat data mata kuliah baru
 * 4. PUT    /api/matakuliah/{kode}   -> Memperbarui data mata kuliah
 * 5. DELETE /api/matakuliah/{kode}   -> Menghapus data mata kuliah
 * Menggunakan Spring REST Controller:
 *  - @RestController menandakan kelas ini controller berbasis REST
 *  - @RequestMapping("/api/matakuliah") menentukan prefix URL endpoint
 */
@RestController
@RequestMapping("/api/matakuliah")
public class MataKuliahController {

    private final MataKuliahService mataKuliahService;
    public MataKuliahController(MataKuliahService mataKuliahService) {
        this.mataKuliahService = mataKuliahService;
    }
    @GetMapping
    public ResponseEntity<List<MataKuliah>> getAllMataKuliah() {
        List<MataKuliah> list = mataKuliahService.findAll();
        return ResponseEntity.ok(list);
    }
    @GetMapping("/{kode}")
    public ResponseEntity<MataKuliah> getMataKuliahByKode(@PathVariable String kode) {
        MataKuliah mk = mataKuliahService.findByKode(kode);
        if (mk == null) {
            return ResponseEntity.notFound().build();  // 404
        }
        return ResponseEntity.ok(mk); // 200 OK
    }
    @PostMapping
    public ResponseEntity<String> createMataKuliah(@RequestBody MataKuliah mk) {
        try {
            mataKuliahService.create(mk);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Mata kuliah berhasil dibuat");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @PutMapping("/{kode}")
    public ResponseEntity<String> updateMataKuliah(
            @PathVariable String kode,
            @RequestBody MataKuliah mk
    ) {
        try {
            mk.setKode(kode);
            mataKuliahService.update(mk);
            return ResponseEntity.ok("Mata kuliah berhasil diupdate");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @DeleteMapping("/{kode}")
    public ResponseEntity<String> deleteMataKuliah(@PathVariable String kode) {
        try {
            mataKuliahService.delete(kode);
            return ResponseEntity.ok("Mata kuliah berhasil dihapus");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}

