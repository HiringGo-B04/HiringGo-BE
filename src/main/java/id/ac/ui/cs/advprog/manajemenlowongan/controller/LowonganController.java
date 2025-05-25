package id.ac.ui.cs.advprog.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.service.LowonganService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/lowongan")
public class LowonganController {

    public static final String ENDPOINT_LOWONGAN = "/api/lowongan";
    public static final String LOWONGAN = "/user/lowongan";
    public static final String DASHBOARD_LECTURER = "/lecturer/dashboard";
    public static final String LOWONGAN_DOSEN = "/lecturer/lowongan";

    private final LowonganService lowonganService;
    private final JwtUtil jwtUtil;

    public LowonganController(LowonganService lowonganService, JwtUtil jwtUtil) {
        this.lowonganService = lowonganService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(LOWONGAN_DOSEN)
    public ResponseEntity<Lowongan> addLowongan(@RequestBody Lowongan lowongan) {
        Lowongan createdLowongan = lowonganService.addLowongan(lowongan);
        return ResponseEntity.ok(createdLowongan);
    }

    @PatchMapping(LOWONGAN_DOSEN)
    public ResponseEntity<Map<String, Object>> updateLowongan(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Lowongan lowongan) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Extract userId from token
            String token = authHeader.substring(7); // Remove "Bearer "
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            UUID dosenId = UUID.fromString(userIdStr);

            // Validate input
            if (lowongan.getId() == null) {
                throw new IllegalArgumentException("Lowongan id tidak boleh kosong");
            }

            return lowonganService.updateLowongan(lowongan.getId(), lowongan, dosenId);
        }
        catch (Exception e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(LOWONGAN)
    public ResponseEntity<List<Lowongan>> getAllLowongan() {
        try {
            List<Lowongan> lowongans = lowonganService.getLowongan();
            return ResponseEntity.ok(lowongans);
        } catch (RuntimeException e) {
            // Menangkap exception dan melemparnya kembali untuk ditangani oleh @ExceptionHandler
            throw new LowonganNotFoundException("Lowongan masih kosong");
        }
    }

    @GetMapping(LOWONGAN+"/{id}")
    public ResponseEntity<Lowongan> getLowonganById(@PathVariable UUID id) {
        try {
            Lowongan lowongan = lowonganService.getLowonganById(id);
            return ResponseEntity.ok(lowongan);
        } catch (RuntimeException e) {
            // Menangkap exception dan melemparnya kembali untuk ditangani oleh @ExceptionHandler
            throw new LowonganNotFoundException("Lowongan dengan ID " + id + " tidak ditemukan");
        }
    }

    @GetMapping(DASHBOARD_LECTURER)
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