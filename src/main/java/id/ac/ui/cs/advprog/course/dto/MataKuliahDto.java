package id.ac.ui.cs.advprog.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Data‑transfer object untuk entitas MataKuliah.
 * Dipakai sebagai payload masuk/keluar layer web agar
 *  • tidak mengekspos entity JPA secara langsung
 *  • dapat divalidasi dengan anotasi Bean Validation.
 */
public record MataKuliahDto(

        @NotBlank String kode,
        @NotBlank String nama,
        @Min(0) int sks,
        String deskripsi,
        @NotNull List<String> dosenPengampu

) { }
