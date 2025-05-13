package id.ac.ui.cs.advprog.mendaftarlowongan.controller;

import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.service.LamaranService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lamaran")
public class LamaranController {

    private final LamaranService lamaranService;

    public LamaranController(LamaranService lamaranService) {
        this.lamaranService = lamaranService;
    }

    @PostMapping
    public ResponseEntity<Lamaran> createLamaran(@RequestBody Lamaran lamaran) {
        return null;
    }

    @GetMapping("/lowongan/{id}")
    public ResponseEntity<List<Lamaran>> getLamaranByLowonganId(@PathVariable UUID id) {
        return null;
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptLamaran(@PathVariable UUID id) {
        return null;
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectLamaran(@PathVariable UUID id) {
        return null;
    }
}