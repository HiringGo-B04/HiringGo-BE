package id.ac.ui.cs.advprog.course.dto;

import jakarta.validation.constraints.Min;

public record MataKuliahPatch(
        @Min(0) Integer sks,
        String          deskripsi
) {}
