package id.ac.ui.cs.advprog.mendaftarlowongan.controller;

import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.mendaftarlowongan.dto.LamaranDTO;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.service.LamaranService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/lamaran")
public class LamaranController {

    private final LamaranService lamaranService;
    private final JwtUtil jwtUtil; // Inject instead of creating new

    public LamaranController(LamaranService lamaranService, JwtUtil jwtUtil) {
        this.lamaranService = lamaranService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/student/add")
    public ResponseEntity<Lamaran> createLamaran(@RequestHeader("Authorization") String authHeader,
                                                 @RequestBody LamaranDTO lamaranDTO) {
        String token = authHeader.replace("Bearer ", "");
        String userIdFromToken = jwtUtil.getUserIdFromToken(token);

        Lamaran created = lamaranService.createLamaran(lamaranDTO, UUID.fromString(userIdFromToken));
        return ResponseEntity.ok(created);
    }

    @GetMapping("/user/get-by-lowongan/{id}")
    public ResponseEntity<List<Lamaran>> getLamaranByLowonganId(@PathVariable UUID id) {
        List<Lamaran> lamarans = lamaranService.getLamaranByLowonganId(id);
        return ResponseEntity.ok(lamarans);
    }

    @PostMapping("/lecturer/accept/{id}")
    public ResponseEntity<Void> acceptLamaran(@PathVariable UUID id) {
        lamaranService.acceptLamaran(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lecturer/reject/{id}")
    public ResponseEntity<Void> rejectLamaran(@PathVariable UUID id) {
        lamaranService.rejectLamaran(id);
        return ResponseEntity.ok().build();
    }
}