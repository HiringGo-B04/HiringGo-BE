package id.ac.ui.cs.advprog.mendaftarlowongan.service;

import id.ac.ui.cs.advprog.mendaftarlowongan.dto.LamaranDTO;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface LamaranService {
    CompletableFuture<List<Lamaran>> getLamaran();
    CompletableFuture<Lamaran> getLamaranById(UUID id);
    CompletableFuture<Lamaran> createLamaran(LamaranDTO lamaran, UUID uuid);
    CompletableFuture<Lamaran> updateLamaran(UUID id, Lamaran lamaran);
    CompletableFuture<Void> deleteLamaran(UUID id);
    CompletableFuture<Boolean> isLamaranExists(Lamaran lamaran);
    CompletableFuture<Void> validateLamaran(Lamaran lamaran);
    CompletableFuture<List<Lamaran>> getLamaranByLowonganId(UUID idLowongan);
    CompletableFuture<Void> acceptLamaran(UUID id);
    CompletableFuture<Void> rejectLamaran(UUID id);
    Lamaran toEntity(LamaranDTO lamaranDTO);
}