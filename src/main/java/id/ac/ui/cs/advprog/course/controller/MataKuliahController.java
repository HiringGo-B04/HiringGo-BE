package id.ac.ui.cs.advprog.course.controller;

import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.service.MataKuliahService;
import id.ac.ui.cs.advprog.course.service.AsyncMataKuliahService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class MataKuliahController {

    private final MataKuliahService service;
    private final AsyncMataKuliahService asyncService;

    /* ========== EXISTING SYNCHRONOUS ENDPOINTS (UNCHANGED) ========== */

    /* ---------- PUBLIC (tanpa token) ---------- */

    @GetMapping("/public/matakuliah")
    public ResponseEntity<Page<MataKuliahDto>> listPublic(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/public/matakuliah/{kode}")
    public ResponseEntity<MataKuliahDto> getPublic(@PathVariable String kode) {
        var dto = service.findByKode(kode);
        return (dto == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    /* ---------- READ-ONLY untuk user bertoken ---------- */

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT','LECTURER')")
    @GetMapping({"/student/matakuliah",
            "/lecturer/matakuliah",
            "/user/matakuliah"})
    public ResponseEntity<Page<MataKuliahDto>> listAuth(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT','LECTURER')")
    @GetMapping({"/student/matakuliah/{kode}",
            "/lecturer/matakuliah/{kode}",
            "/user/matakuliah/{kode}"})
    public ResponseEntity<MataKuliahDto> getAuth(@PathVariable String kode) {
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

    /**
     * Batch course creation - the most useful async operation
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/matakuliah/batch/async")
    public DeferredResult<ResponseEntity<List<MataKuliahDto>>> createMultipleAsync(
            @Valid @RequestBody List<MataKuliahDto> courses) {
        DeferredResult<ResponseEntity<List<MataKuliahDto>>> deferredResult =
                new DeferredResult<>(30000L); // 30 second timeout for batch operations

        asyncService.createMultipleAsync(courses)
                .thenAccept(created ->
                        deferredResult.setResult(ResponseEntity.ok(created)))
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(
                            ResponseEntity.badRequest().build());
                    return null;
                });

        return deferredResult;
    }

    /**
     * Advanced search - useful for complex filtering
     */
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT','LECTURER')")
    @GetMapping("/user/matakuliah/search/async")
    public DeferredResult<ResponseEntity<List<MataKuliahDto>>> searchCoursesAsync(@RequestParam String q) {
        DeferredResult<ResponseEntity<List<MataKuliahDto>>> deferredResult =
                new DeferredResult<>(10000L);

        asyncService.searchCoursesAsync(q)
                .thenAccept(results ->
                        deferredResult.setResult(ResponseEntity.ok(results)))
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(
                            ResponseEntity.internalServerError().build());
                    return null;
                });

        return deferredResult;
    }

    /**
     * Get courses by lecturer - involves complex joins
     */
    @PreAuthorize("hasAnyRole('ADMIN','LECTURER')")
    @GetMapping("/lecturer/matakuliah/my-courses/async")
    public DeferredResult<ResponseEntity<List<MataKuliahDto>>> getMyCourses(Authentication authentication) {
        DeferredResult<ResponseEntity<List<MataKuliahDto>>> deferredResult =
                new DeferredResult<>(8000L);

        UUID lecturerId = UUID.fromString(authentication.getName());

        asyncService.getCoursesByLecturerAsync(lecturerId)
                .thenAccept(courses ->
                        deferredResult.setResult(ResponseEntity.ok(courses)))
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(
                            ResponseEntity.internalServerError().build());
                    return null;
                });

        return deferredResult;
    }
}