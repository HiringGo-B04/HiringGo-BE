package id.ac.ui.cs.advprog.authjwt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LecturerDto(
        @NotBlank
        String username,

        @NotBlank
        String password,

        @NotNull
        @NotBlank
        String nip,

        @NotNull
        @NotBlank
        String fullName
) {}
