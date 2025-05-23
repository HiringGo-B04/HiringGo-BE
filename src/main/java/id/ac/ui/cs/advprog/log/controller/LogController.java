package id.ac.ui.cs.advprog.log.controller;

import id.ac.ui.cs.advprog.log.dto.LogDTO;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.exception.BadRequestException;
import id.ac.ui.cs.advprog.log.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.log.exception.ForbiddenException;
import id.ac.ui.cs.advprog.log.model.Log;
import id.ac.ui.cs.advprog.log.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    private LogService logService;

    // ========== Manajemen Log (Student) ==========

    @GetMapping("/student/lowongan/{lowonganId}")
    public ResponseEntity<?> getStudentLogs(@PathVariable UUID lowonganId) {
        try {
            UUID mahasiswaId = getCurrentUserId();
            List<Log> logs = logService.findByMahasiswaAndLowongan(mahasiswaId, lowonganId);
            return ResponseEntity.ok(logs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @GetMapping("/student/{logId}")
    public ResponseEntity<?> getStudentLog(@PathVariable UUID logId) {
        try {
            UUID mahasiswaId = getCurrentUserId();
            Log log = logService.findByIdForMahasiswa(logId, mahasiswaId);
            return ResponseEntity.ok(log);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @PostMapping("/student")
    public ResponseEntity<?> createStudentLog(@RequestBody LogDTO logDTO) {
        try {
            // Validate required fields
            if (logDTO.getJudul() == null || logDTO.getJudul().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Judul tidak boleh kosong"));
            }
            if (logDTO.getKeterangan() == null || logDTO.getKeterangan().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Keterangan tidak boleh kosong"));
            }
            if (logDTO.getKategori() == null || logDTO.getKategori().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Kategori tidak boleh kosong"));
            }
            if (logDTO.getTanggalLog() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Tanggal log tidak boleh kosong"));
            }
            if (logDTO.getWaktuMulai() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Waktu mulai tidak boleh kosong"));
            }
            if (logDTO.getWaktuSelesai() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Waktu selesai tidak boleh kosong"));
            }
            if (logDTO.getIdLowongan() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "ID Lowongan tidak boleh kosong"));
            }

            UUID mahasiswaId = getCurrentUserId();
            Log createdLog = logService.createLogForMahasiswa(logDTO, mahasiswaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLog);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (BadRequestException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @PutMapping("/student/{logId}")
    public ResponseEntity<?> updateStudentLog(@PathVariable UUID logId, @RequestBody LogDTO logDTO) {
        try {
            // Validate required fields
            if (logDTO.getJudul() == null || logDTO.getJudul().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Judul tidak boleh kosong"));
            }
            if (logDTO.getKeterangan() == null || logDTO.getKeterangan().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Keterangan tidak boleh kosong"));
            }
            if (logDTO.getKategori() == null || logDTO.getKategori().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Kategori tidak boleh kosong"));
            }
            if (logDTO.getTanggalLog() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Tanggal log tidak boleh kosong"));
            }
            if (logDTO.getWaktuMulai() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Waktu mulai tidak boleh kosong"));
            }
            if (logDTO.getWaktuSelesai() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Waktu selesai tidak boleh kosong"));
            }

            UUID mahasiswaId = getCurrentUserId();
            Log updatedLog = logService.updateLogForMahasiswa(logId, logDTO, mahasiswaId);
            return ResponseEntity.ok(updatedLog);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (BadRequestException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @DeleteMapping("/student/{logId}")
    public ResponseEntity<?> deleteStudentLog(@PathVariable UUID logId) {
        try {
            UUID mahasiswaId = getCurrentUserId();
            logService.deleteLogForMahasiswa(logId, mahasiswaId);
            return ResponseEntity.ok(Map.of("message", "Log berhasil dihapus"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @GetMapping("/student/honor")
    public ResponseEntity<?> getStudentHonor(
            @RequestParam UUID lowonganId,
            @RequestParam int tahun,
            @RequestParam int bulan) {
        try {
            // Validate month and year
            if (bulan < 1 || bulan > 12) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Bulan harus antara 1-12"));
            }
            if (tahun < 2020 || tahun > 2030) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Tahun tidak valid"));
            }

            UUID mahasiswaId = getCurrentUserId();
            Map<String, Object> honorData = logService.calculateHonorData(mahasiswaId, lowonganId, tahun, bulan);
            return ResponseEntity.ok(honorData);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    // ========== Periksa Log (Lecturer) ==========

    @GetMapping("/lecturer")
    public ResponseEntity<?> getLecturerLogs() {
        try {
            UUID dosenId = getCurrentUserId();
            List<Log> logs = logService.findByDosen(dosenId);
            return ResponseEntity.ok(logs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @GetMapping("/lecturer/lowongan/{lowonganId}")
    public ResponseEntity<?> getLecturerLogsByLowongan(@PathVariable UUID lowonganId) {
        try {
            UUID dosenId = getCurrentUserId();
            List<Log> logs = logService.findByLowonganAndDosen(lowonganId, dosenId);
            return ResponseEntity.ok(logs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @PatchMapping("/lecturer/{logId}/status")
    public ResponseEntity<?> updateLogStatus(
            @PathVariable UUID logId,
            @RequestParam String status) {
        try {
            UUID dosenId = getCurrentUserId();

            // Validate status parameter
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Status tidak boleh kosong"));
            }

            StatusLog statusLog;
            try {
                statusLog = StatusLog.valueOf(status.toUpperCase());
                if (statusLog == StatusLog.MENUNGGU) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Status tidak valid. Gunakan DITERIMA atau DITOLAK"));
                }
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Status tidak valid. Gunakan DITERIMA atau DITOLAK"));
            }

            Log verifiedLog = logService.verifyLog(logId, statusLog, dosenId);
            return ResponseEntity.ok(Map.of(
                    "message", "Log berhasil " + (statusLog == StatusLog.DITERIMA ? "diterima" : "ditolak"),
                    "log", verifiedLog
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    // Helper method untuk mendapatkan current user ID
    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new BadRequestException("User tidak terautentikasi");
        }
        try {
            return UUID.fromString(auth.getName());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("User ID tidak valid");
        }
    }
}