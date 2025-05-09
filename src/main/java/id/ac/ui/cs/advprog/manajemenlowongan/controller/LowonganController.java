package id.ac.ui.cs.advprog.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.service.LowonganService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/lowongan")
public class LowonganController {

    private final LowonganService lowonganService;

    public LowonganController(LowonganService lowonganService) {
        this.lowonganService = lowonganService;
    }

    @PostMapping
    public ResponseEntity<Lowongan> addLowongan(@RequestBody Lowongan lowongan) {
        Lowongan createdLowongan = lowonganService.addLowongan(lowongan);
        return ResponseEntity.ok(createdLowongan);
    }

    @GetMapping("/lowongan/{id}")
    public ResponseEntity<Lowongan> getLowonganById(@PathVariable UUID id) {
        try {
            Lowongan lowongan = lowonganService.getLowonganById(id);
            return ResponseEntity.ok(lowongan);
        } catch (RuntimeException e) {
            // Menangkap exception dan melemparnya kembali untuk ditangani oleh @ExceptionHandler
            throw new LowonganNotFoundException("Lowongan dengan ID " + id + " tidak ditemukan");
        }
    }

    // Exception handler untuk LowonganNotFoundException
    @ExceptionHandler(LowonganNotFoundException.class)
    public ResponseEntity<String> handleLowonganNotFoundException(LowonganNotFoundException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    // Custom exception class untuk lowongan tidak ditemukan
    public static class LowonganNotFoundException extends RuntimeException {
        public LowonganNotFoundException(String message) {
            super(message);
        }
    }
}