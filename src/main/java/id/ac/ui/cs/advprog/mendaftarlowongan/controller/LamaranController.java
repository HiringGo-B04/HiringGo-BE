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

@RestController
@RequestMapping("/api/lamaran")
public class LamaranController {

    @Autowired
    private LamaranService lamaranService;

    @Autowired
    private JwtUtil jwtUtil;

    public static final String ENDPOINT_CREATE_LAMARAN = "/student/add";
    public static final String ENDPOINT_GET_LAMARAN_BY_LOWONGAN = "/user/get-by-lowongan/{id}";
    public static final String ENDPOINT_ACCEPT_LAMARAN = "/lecturer/accept/{id}";
    public static final String ENDPOINT_REJECT_LAMARAN = "/lecturer/reject/{id}";
    public static final String ENDPOINT_GET_ALL_LAMARAN = "/user/all";
    public static final String ENDPOINT_GET_LAMARAN_BY_ID = "/user/{id}";
    public static final String ENDPOINT_UPDATE_LAMARAN = "/lecturer/{id}";
    public static final String ENDPOINT_DELETE_LAMARAN = "/lecturer/{id}";


    @PostMapping(ENDPOINT_CREATE_LAMARAN)
    public ResponseEntity<Lamaran> createLamaran(
            @RequestBody LamaranDTO lamaranDTO,
            @RequestHeader("Authorization") String authHeader) {

        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            UUID userId = UUID.fromString(userIdStr);

            Lamaran lamaran = lamaranService.createLamaran(lamaranDTO, userId).join();
            return ResponseEntity.ok(lamaran);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(ENDPOINT_GET_LAMARAN_BY_LOWONGAN)
    public ResponseEntity<List<Lamaran>> getLamaranByLowonganId(@PathVariable UUID id) {
        try {
            List<Lamaran> lamaranList = lamaranService.getLamaranByLowonganId(id).join();
            return ResponseEntity.ok(lamaranList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(ENDPOINT_ACCEPT_LAMARAN)
    public ResponseEntity<String> acceptLamaran(@PathVariable UUID id) {
        lamaranService.acceptLamaran(id).join();
        return ResponseEntity.ok("Lamaran accepted successfully");
    }

    @PostMapping(ENDPOINT_REJECT_LAMARAN)
    public ResponseEntity<String> rejectLamaran(@PathVariable UUID id) {
        try {
            lamaranService.rejectLamaran(id).join();
            return ResponseEntity.ok("Lamaran rejected successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to reject lamaran");
        }
    }

    @GetMapping(ENDPOINT_GET_ALL_LAMARAN)
    public ResponseEntity<List<Lamaran>> getAllLamaran() {
        try {
            List<Lamaran> lamaranList = lamaranService.getLamaran().join();
            return ResponseEntity.ok(lamaranList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(ENDPOINT_GET_LAMARAN_BY_ID)
    public ResponseEntity<Lamaran> getLamaranById(@PathVariable UUID id) {
        try {
            Lamaran lamaran = lamaranService.getLamaranById(id).join();
            if (lamaran != null) {
                return ResponseEntity.ok(lamaran);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(ENDPOINT_UPDATE_LAMARAN)
    public ResponseEntity<Lamaran> updateLamaran(
            @PathVariable UUID id,
            @RequestBody Lamaran lamaran) {

        try {
            Lamaran updated = lamaranService.updateLamaran(id, lamaran).join();
            if (updated != null) {
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(ENDPOINT_DELETE_LAMARAN)
    public ResponseEntity<String> deleteLamaran(@PathVariable UUID id) {
        try {
            lamaranService.deleteLamaran(id).join();
            return ResponseEntity.ok("Lamaran deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete lamaran");
        }
    }
}