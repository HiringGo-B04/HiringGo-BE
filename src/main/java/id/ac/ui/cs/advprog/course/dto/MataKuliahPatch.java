package id.ac.ui.cs.advprog.course.dto;

import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

public record MataKuliahPatch(

        @Min(1)
        Integer sks,

        String deskripsi,

        List<UUID> dosenPengampu
) {}
