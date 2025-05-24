package id.ac.ui.cs.advprog.mendaftarlowongan.service;

import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.mendaftarlowongan.dto.LamaranDTO;
import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.exception.*;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.repository.LamaranRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
public class LamaranServiceImpl implements LamaranService {

    @Autowired
    private LamaranRepository lamaranRepository;

    @Autowired
    private LowonganRepository lowonganRepository;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;

    // Default constructor for Spring
    public LamaranServiceImpl() {
    }

    // Constructor for testing purposes
    public LamaranServiceImpl(LamaranRepository lamaranRepository) {
        this.lamaranRepository = lamaranRepository;
    }

    public LamaranServiceImpl(LamaranRepository lamaranRepository, LowonganRepository lowonganRepository) {
        this.lamaranRepository = lamaranRepository;
        this.lowonganRepository = lowonganRepository;
    }


    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<Lamaran>> getLamaran() {
        return CompletableFuture.supplyAsync(() -> lamaranRepository.findAll(), executor);
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Lamaran> getLamaranById(UUID id) {
        return CompletableFuture.supplyAsync(() -> lamaranRepository.findById(id)
                .orElseThrow(() -> new LamaranNotFoundExceptionException(id.toString())), executor);
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Lamaran> createLamaran(LamaranDTO lamaranDTO, UUID userIdFromToken) {
        return CompletableFuture.supplyAsync(() -> {
            // Validasi idMahasiswa harus sama dengan userId pada token
            if (!lamaranDTO.getIdMahasiswa().equals(userIdFromToken)) {
                throw new RuntimeException("ID Mahasiswa tidak sesuai dengan userId pada token.");
            }

            Lamaran lamaran = toEntity(lamaranDTO);

            return validateLamaran(lamaran)
                    .thenCompose(v -> CompletableFuture.supplyAsync(() -> {
                        // Simpan lamaran terlebih dahulu
                        Lamaran savedLamaran = lamaranRepository.save(lamaran);

                        // Cari lowongan terkait
                        Optional<Lowongan> optionalLowongan = lowonganRepository.findById(lamaran.getIdLowongan());
                        if (optionalLowongan.isEmpty()) {
                            throw new RuntimeException("Lowongan tidak ditemukan.");
                        }

                        Lowongan lowongan = optionalLowongan.get();
                        // Tambah totalAsdosRegistered
                        lowongan.setTotalAsdosRegistered(lowongan.getTotalAsdosRegistered() + 1);
                        lowonganRepository.save(lowongan);

                        return savedLamaran;
                    }, executor)).exceptionally(throwable -> {
                        throw new RuntimeException("Error creating lamaran: " + throwable.getMessage());
                    }).join();
        }, executor);
    }


    @Override
    @Async("taskExecutor")
    public CompletableFuture<Lamaran> updateLamaran(UUID id, Lamaran lamaran) {
        return getLamaranById(id)
                .thenCompose(existing -> {
                    if (existing == null) {
                        return CompletableFuture.completedFuture(null);
                    }

                    existing.setIpk(lamaran.getIpk());
                    existing.setSks(lamaran.getSks());
                    existing.setStatus(lamaran.getStatus());
                    existing.setIdMahasiswa(lamaran.getIdMahasiswa());
                    existing.setIdLowongan(lamaran.getIdLowongan());

                    return CompletableFuture.supplyAsync(() ->
                            lamaranRepository.save(existing), executor);
                });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> deleteLamaran(UUID id) {
        return CompletableFuture.runAsync(() -> {
            lamaranRepository.deleteById(id);
        }, executor);
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Boolean> isLamaranExists(Lamaran lamaran) {
        return CompletableFuture.supplyAsync(() -> lamaranRepository.findAll().stream()
                .anyMatch(l -> l.getIdMahasiswa().equals(lamaran.getIdMahasiswa())
                        && l.getIdLowongan().equals(lamaran.getIdLowongan())), executor);
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> validateLamaran(Lamaran lamaran) {
        return CompletableFuture.supplyAsync(() -> {
                    boolean ipkValid = lamaran.getIpk() >= 0 && lamaran.getIpk() <= 4;
                    boolean sksValid = lamaran.getSks() >= 0 && lamaran.getSks() <= 24;

                    if (!ipkValid) {
                        throw new IPKInvalidException();
                    } else if (!sksValid) {
                        throw new SKSInvalidException();
                    }

                    return null;
                }, executor)
                .thenCompose(v -> isLamaranExists(lamaran))
                .thenAccept(exists -> {
                    if (exists) {
                        throw new DuplicateLamaranException();
                    }
                });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<Lamaran>> getLamaranByLowonganId(UUID idLowongan) {
        return CompletableFuture.supplyAsync(() -> lamaranRepository.findAll().stream()
                .filter(l -> l.getIdLowongan().equals(idLowongan))
                .collect(Collectors.toList()), executor);
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> acceptLamaran(UUID id) {
        return getLamaranById(id)
                .thenCompose(lamaran -> {
                    if (lamaran == null) {
                        throw new LamaranNotFoundExceptionException(id.toString());
                    }

                    lamaran.setStatus(StatusLamaran.DITERIMA);

                    return CompletableFuture.supplyAsync(() -> {
                        lamaranRepository.save(lamaran);

                        Lowongan lowongan = lowonganRepository.findById(lamaran.getIdLowongan())
                                .orElseThrow(() -> new LowonganNotFoundExceptionException(lamaran.getIdLowongan().toString()));

                        lowongan.setTotalAsdosAccepted(lowongan.getTotalAsdosAccepted() + 1);
                        lowonganRepository.save(lowongan);

                        return null;
                    }, executor);
                });
    }



    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> rejectLamaran(UUID id) {
        return getLamaranById(id)
                .thenCompose(lamaran -> {
                    if (lamaran == null) {
                        throw new LamaranNotFoundExceptionException(id.toString());
                    } else {
                        lamaran.setStatus(StatusLamaran.DITOLAK);
                        return CompletableFuture.supplyAsync(() -> {
                            lamaranRepository.save(lamaran);
                            return null;
                        }, executor);
                    }
                });
    }

    @Override
    public Lamaran toEntity(LamaranDTO lamaranDTO) {
        return new Lamaran.Builder()
                .sks(lamaranDTO.getSks())
                .ipk(lamaranDTO.getIpk())
                .status(StatusLamaran.MENUNGGU)
                .mahasiswa(lamaranDTO.getIdMahasiswa())
                .lowongan(lamaranDTO.getIdLowongan())
                .build();
    }
}