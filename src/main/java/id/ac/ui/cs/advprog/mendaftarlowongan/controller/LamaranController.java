package id.ac.ui.cs.advprog.mendaftarlowongan.controller;

import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.mendaftarlowongan.dto.LamaranDTO;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.service.LamaranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/lamaran")
public class LamaranController {

    @Autowired
    private LamaranService lamaranService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/student/add")
    public CompletableFuture<ResponseEntity<Lamaran>> createLamaran(
            @RequestBody LamaranDTO lamaranDTO,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7); // Remove "Bearer "
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        UUID userId = UUID.fromString(userIdStr);

        return lamaranService.createLamaran(lamaranDTO, userId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable ->
                        ResponseEntity.badRequest().build());
    }

    @GetMapping("/user/get-by-lowongan/{id}")
    public CompletableFuture<ResponseEntity<List<Lamaran>>> getLamaranByLowonganId(@PathVariable UUID id) {
        return lamaranService.getLamaranByLowonganId(id)
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable ->
                        ResponseEntity.badRequest().build());
    }

    @PostMapping("/lecturer/accept/{id}")
    public CompletableFuture<ResponseEntity<String>> acceptLamaran(@PathVariable UUID id) {
        return lamaranService.acceptLamaran(id)
                .thenApply(v -> ResponseEntity.ok("Lamaran accepted successfully"))
                .exceptionally(throwable ->
                        ResponseEntity.badRequest().body("Failed to accept lamaran"));
    }

    @PostMapping("/lecturer/reject/{id}")
    public CompletableFuture<ResponseEntity<String>> rejectLamaran(@PathVariable UUID id) {
        return lamaranService.rejectLamaran(id)
                .thenApply(v -> ResponseEntity.ok("Lamaran rejected successfully"))
                .exceptionally(throwable ->
                        ResponseEntity.badRequest().body("Failed to reject lamaran"));
    }

    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<List<Lamaran>>> getAllLamaran() {
        return lamaranService.getLamaran()
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable ->
                        ResponseEntity.badRequest().build());
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<Lamaran>> getLamaranById(@PathVariable UUID id) {
        return lamaranService.getLamaranById(id)
                .thenApply(lamaran -> {
                    if (lamaran != null) {
                        return ResponseEntity.ok(lamaran);
                    } else {
                        return ResponseEntity.notFound().<Lamaran>build();
                    }
                })
                .exceptionally(throwable ->
                        ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<Lamaran>> updateLamaran(
            @PathVariable UUID id,
            @RequestBody Lamaran lamaran) {

        return lamaranService.updateLamaran(id, lamaran)
                .thenApply(updated -> {
                    if (updated != null) {
                        return ResponseEntity.ok(updated);
                    } else {
                        return ResponseEntity.notFound().<Lamaran>build();
                    }
                })
                .exceptionally(throwable ->
                        ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteLamaran(@PathVariable UUID id) {
        return lamaranService.deleteLamaran(id)
                .thenApply(v -> ResponseEntity.ok("Lamaran deleted successfully"))
                .exceptionally(throwable ->
                        ResponseEntity.badRequest().body("Failed to delete lamaran"));
    }
}