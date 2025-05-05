package id.ac.ui.cs.advprog.mendaftarlowongan.service;

import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;

import java.util.List;
import java.util.UUID;

public interface LamaranService {
    List<Lamaran> getLamaran();
    Lamaran getLamaranById(UUID id);
    Lamaran createLamaran(Lamaran lamaran);
    Lamaran updateLamaran(UUID id, Lamaran lamaran);
    void deleteLamaran(UUID id);
    boolean isLamaranExists(Lamaran lamaran);
    boolean validateLamaran(Lamaran lamaran);
    List<Lamaran> getLamaranByLowonganId(UUID idLowongan);
    void acceptLamaran(UUID id);
    void rejectLamaran(UUID id);
}
