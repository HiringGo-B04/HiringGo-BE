package id.ac.ui.cs.advprog.mendaftarlowongan;

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
        Lamaran created = lamaranService.createLamaran(lamaran);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/lowongan/{id}")
    public ResponseEntity<List<Lamaran>> getLamaranByLowonganId(@PathVariable UUID id) {
        List<Lamaran> lamarans = lamaranService.getLamaranByLowonganId(id);
        return ResponseEntity.ok(lamarans);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptLamaran(@PathVariable UUID id) {
        lamaranService.acceptLamaran(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectLamaran(@PathVariable UUID id) {
        lamaranService.rejectLamaran(id);
        return ResponseEntity.ok().build();
    }
}