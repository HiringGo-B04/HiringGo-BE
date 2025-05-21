package id.ac.ui.cs.advprog.log.controller;

import id.ac.ui.cs.advprog.log.dto.LogDTO;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.exception.BadRequestException;
import id.ac.ui.cs.advprog.log.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.log.model.Log;
import id.ac.ui.cs.advprog.log.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    private LogService logService;

    // Manajemen Log
    @GetMapping("/student/lowongan/{lowonganId}")
    public ResponseEntity<?> getStudentLogs(@PathVariable UUID lowonganId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UUID mahasiswaId = UUID.fromString(auth.getName());

            List<Log> logs = logService.findByMahasiswaAndLowongan(mahasiswaId, lowonganId);
            return ResponseEntity.ok(logs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @GetMapping("/student/{logId}")
    public ResponseEntity<?> getStudentLog(@PathVariable UUID logId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UUID mahasiswaId = UUID.fromString(auth.getName());

            Log log = logService.findById(logId);

            if (!log.getIdMahasiswa().equals(mahasiswaId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Anda tidak dapat mengakses log milik mahasiswa lain"));
            }

            return ResponseEntity.ok(log);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @PostMapping("/student")
    public ResponseEntity<?> createStudentLog(@RequestBody LogDTO logDTO, @RequestParam UUID dosenId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UUID mahasiswaId = UUID.fromString(auth.getName());

            Log createdLog = logService.createLogForMahasiswa(logDTO, mahasiswaId, dosenId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLog);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @PutMapping("/student/{logId}")
    public ResponseEntity<?> updateStudentLog(@PathVariable UUID logId, @RequestBody LogDTO logDTO) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UUID mahasiswaId = UUID.fromString(auth.getName());

            Log updatedLog = logService.updateLogForMahasiswa(logId, logDTO, mahasiswaId);
            return ResponseEntity.ok(updatedLog);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @DeleteMapping("/student/{logId}")
    public ResponseEntity<?> deleteStudentLog(@PathVariable UUID logId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UUID mahasiswaId = UUID.fromString(auth.getName());

            logService.deleteLogForMahasiswa(logId, mahasiswaId);
            return ResponseEntity.ok(Map.of("message", "Log berhasil dihapus"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @GetMapping("/student/honor")
    public ResponseEntity<?> getStudentHonor(
            @RequestParam UUID lowonganId,
            @RequestParam int tahun,
            @RequestParam int bulan) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UUID mahasiswaId = UUID.fromString(auth.getName());

            double honor = logService.calculateHonor(mahasiswaId, lowonganId, tahun, bulan);

            Map<String, Object> response = new HashMap<>();
            response.put("bulan", bulan);
            response.put("tahun", tahun);
            response.put("lowonganId", lowonganId);
            response.put("honor", honor);
            response.put("formattedHonor", String.format("Rp %,.2f", honor));

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    // Periksa Log
    @GetMapping("/lecturer")
    public ResponseEntity<?> getLecturerLogs() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UUID dosenId = UUID.fromString(auth.getName());

            List<Log> logs = logService.findByDosen(dosenId);
            return ResponseEntity.ok(logs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @GetMapping("/lecturer/lowongan/{lowonganId}")
    public ResponseEntity<?> getLecturerLogsByLowongan(@PathVariable UUID lowonganId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UUID dosenId = UUID.fromString(auth.getName());

            List<Log> logs = logService.findByLowonganAndDosen(lowonganId, dosenId);
            return ResponseEntity.ok(logs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }


    @PatchMapping("/lecturer/{logId}/status")
    public ResponseEntity<?> updateLogStatus(
            @PathVariable UUID logId,
            @RequestParam String status) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UUID dosenId = UUID.fromString(auth.getName());

            StatusLog statusLog;
            try {
                statusLog = StatusLog.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status tidak valid. Gunakan DITERIMA atau DITOLAK"));
            }

            Log verifiedLog = logService.verifyLog(logId, statusLog, dosenId);
            return ResponseEntity.ok(verifiedLog);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }
}