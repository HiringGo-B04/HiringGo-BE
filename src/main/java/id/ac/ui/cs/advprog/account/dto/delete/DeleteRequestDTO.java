package id.ac.ui.cs.advprog.account.dto.delete;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeleteRequestDTO (
        @NotBlank
        @NotNull
        String email
) {}
