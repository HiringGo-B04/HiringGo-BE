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
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    /* ========== NEW ASYNCHRONOUS ENDPOINTS ========== */

    /* ---------- PUBLIC ASYNC ---------- */

    @GetMapping("/public/matakuliah/async")
    public DeferredResult<ResponseEntity<Page<MataKuliahDto>>> listPublicAsync(Pageable pageable) {
        DeferredResult<ResponseEntity<Page<MataKuliahDto>>> deferredResult =
                new DeferredResult<>(10000L); // 10 second timeout

        asyncService.findAllAsync(pageable)
                .thenAccept(courses ->
                        deferredResult.setResult(ResponseEntity.ok(courses)))
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(
                            ResponseEntity.internalServerError().build());
                    return null;
                });

        return deferredResult;
    }

    @GetMapping("/public/matakuliah/{kode}/async")
    public DeferredResult<ResponseEntity<MataKuliahDto>> getPublicAsync(@PathVariable String kode) {
        DeferredResult<ResponseEntity<MataKuliahDto>> deferredResult =
                new DeferredResult<>(8000L); // 8 second timeout

        asyncService.findByKodeAsync(kode)
                .thenAccept(course -> {
                    if (course == null) {
                        deferredResult.setResult(ResponseEntity.notFound().build());
                    } else {
                        deferredResult.setResult(ResponseEntity.ok(course));
                    }
                })
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(
                            ResponseEntity.internalServerError().build());
                    return null;
                });

        return deferredResult;
    }

    /* ---------- AUTHENTICATED USER ASYNC ---------- */

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT','LECTURER')")
    @GetMapping({"/student/matakuliah/async",
            "/lecturer/matakuliah/async",
            "/user/matakuliah/async"})
    public DeferredResult<ResponseEntity<Page<MataKuliahDto>>> listAuthAsync(Pageable pageable) {
        DeferredResult<ResponseEntity<Page<MataKuliahDto>>> deferredResult =
                new DeferredResult<>(10000L);

        asyncService.findAllAsync(pageable)
                .thenAccept(courses ->
                        deferredResult.setResult(ResponseEntity.ok(courses)))
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(
                            ResponseEntity.internalServerError().build());
                    return null;
                });

        return deferredResult;
    }

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT','LECTURER')")
    @GetMapping({"/student/matakuliah/{kode}/async",
            "/lecturer/matakuliah/{kode}/async",
            "/user/matakuliah/{kode}/async"})
    public DeferredResult<ResponseEntity<MataKuliahDto>> getAuthAsync(@PathVariable String kode) {
        DeferredResult<ResponseEntity<MataKuliahDto>> deferredResult =
                new DeferredResult<>(8000L);

        asyncService.findByKodeAsync(kode)
                .thenAccept(course -> {
                    if (course == null) {
                        deferredResult.setResult(ResponseEntity.notFound().build());
                    } else {
                        deferredResult.setResult(ResponseEntity.ok(course));
                    }
                })
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(
                            ResponseEntity.internalServerError().build());
                    return null;
                });

        return deferredResult;
    }

    /* ---------- ADMIN ASYNC CRUD ---------- */

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/matakuliah/async")
    public DeferredResult<ResponseEntity<MataKuliahDto>> createAsync(@Valid @RequestBody MataKuliahDto dto) {
        DeferredResult<ResponseEntity<MataKuliahDto>> deferredResult =
                new DeferredResult<>(15000L); // 15 second timeout for creation

        asyncService.createAsync(dto)
                .thenAccept(saved -> {
                    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                            .path("/{kode}").buildAndExpand(saved.kode()).toUri();
                    deferredResult.setResult(ResponseEntity.created(location).body(saved));
                })
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(
                            ResponseEntity.badRequest().build());
                    return null;
                });

        return deferredResult;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/matakuliah/{kode}/async")
    public DeferredResult<ResponseEntity<MataKuliahDto>> replaceAsync(@PathVariable String kode,
                                                                      @Valid @RequestBody MataKuliahDto dto) {
        DeferredResult<ResponseEntity<MataKuliahDto>> deferredResult =
                new DeferredResult<>(12000L); // 12 second timeout

        asyncService.updateAsync(kode, dto)
                .thenAccept(updated ->
                        deferredResult.setResult(ResponseEntity.ok(updated)))
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(
                            ResponseEntity.badRequest().build());
                    return null;
                });

        return deferredResult;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/matakuliah/{kode}/async")
    public DeferredResult<ResponseEntity<MataKuliahDto>> patchAsync(@PathVariable String kode,
                                                                    @Valid @RequestBody MataKuliahPatch patch) {
        DeferredResult<ResponseEntity<MataKuliahDto>> deferredResult =
                new DeferredResult<>(10000L);

        asyncService.partialUpdateAsync(kode, patch)
                .thenAccept(updated ->
                        deferredResult.setResult(ResponseEntity.ok(updated)))
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(
                            ResponseEntity.badRequest().build());
                    return null;
                });

        return deferredResult;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/matakuliah/{kode}/async")
    public DeferredResult<ResponseEntity<Void>> deleteAsync(@PathVariable String kode) {
        DeferredResult<ResponseEntity<Void>> deferredResult =
                new DeferredResult<>(10000L);

        asyncService.deleteAsync(kode)
                .thenAccept(result ->
                        deferredResult.setResult(ResponseEntity.noContent().build()))
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(
                            ResponseEntity.badRequest().build());
                    return null;
                });

        return deferredResult;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/matakuliah/{kode}/dosen/{userId}/async")
    public DeferredResult<ResponseEntity<Void>> addLecturerAsync(@PathVariable String kode,
                                                                 @PathVariable UUID userId) {
        DeferredResult<ResponseEntity<Void>> deferredResult =
                new DeferredResult<>(10000L);

        asyncService.addLecturerAsync(kode, userId)
                .thenAccept(result ->
                        deferredResult.setResult(ResponseEntity.noContent().build()))
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(
                            ResponseEntity.badRequest().build());
                    return null;
                });

        return deferredResult;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/matakuliah/{kode}/dosen/{userId}/async")
    public DeferredResult<ResponseEntity<Void>> removeLecturerAsync(@PathVariable String kode,
                                                                    @PathVariable UUID userId) {
        DeferredResult<ResponseEntity<Void>> deferredResult =
                new DeferredResult<>(8000L);

        asyncService.removeLecturerAsync(kode, userId)
                .thenAccept(result ->
                        deferredResult.setResult(ResponseEntity.noContent().build()))
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(
                            ResponseEntity.badRequest().build());
                    return null;
                });

        return deferredResult;
    }

    /* ========== ADVANCED ASYNC FEATURES ========== */

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

    /* ========== ALTERNATIVE COMPLETABLE FUTURE ENDPOINTS ========== */

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT','LECTURER')")
    @GetMapping("/user/matakuliah/future")
    public CompletableFuture<ResponseEntity<Page<MataKuliahDto>>> listWithCompletableFuture(Pageable pageable) {
        return asyncService.findAllAsync(pageable)
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable ->
                        ResponseEntity.internalServerError().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT','LECTURER')")
    @GetMapping("/user/matakuliah/{kode}/future")
    public CompletableFuture<ResponseEntity<MataKuliahDto>> getWithCompletableFuture(@PathVariable String kode) {
        return asyncService.findByKodeAsync(kode)
                .thenApply(course -> {
                    if (course == null) {
                        return ResponseEntity.notFound().<MataKuliahDto>build();
                    } else {
                        return ResponseEntity.ok(course);
                    }
                })
                .exceptionally(throwable ->
                        ResponseEntity.internalServerError().build());
    }
}