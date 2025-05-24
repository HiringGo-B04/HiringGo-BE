package id.ac.ui.cs.advprog.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.service.LowonganService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/lowongan")
public class LowonganController {

    private final LowonganService lowonganService;
    private final JwtUtil jwtUtil;

    public LowonganController(LowonganService lowonganService, JwtUtil jwtUtil) {
        this.lowonganService = lowonganService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/user/add")
    public ResponseEntity<Lowongan> addLowongan(@RequestBody Lowongan lowongan) {
        Lowongan createdLowongan = lowonganService.addLowongan(lowongan);
        return ResponseEntity.ok(createdLowongan);
    }

    @GetMapping("/user/get")
    public ResponseEntity<List<Lowongan>> getLowongan() {
        try {
            List<Lowongan> lowongans = lowonganService.getLowongan();
            return ResponseEntity.ok(lowongans);
        } catch (RuntimeException e) {
            // Menangkap exception dan melemparnya kembali untuk ditangani oleh @ExceptionHandler
            throw new LowonganNotFoundException("Lowongan masih kosong");
        }
    }

    @GetMapping("/user/get/{id}")
    public ResponseEntity<Lowongan> getLowonganById(@PathVariable UUID id) {
        try {
            Lowongan lowongan = lowonganService.getLowonganById(id);
            return ResponseEntity.ok(lowongan);
        } catch (RuntimeException e) {
            // Menangkap exception dan melemparnya kembali untuk ditangani oleh @ExceptionHandler
            throw new LowonganNotFoundException("Lowongan dengan ID " + id + " tidak ditemukan");
        }
    }

    @GetMapping("/lecturer/get")
    public ResponseEntity<Map<String, Object>> getLecturerDataById(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            UUID userId = UUID.fromString(userIdStr);

            return lowonganService.getLowonganByDosen(userId);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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