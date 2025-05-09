package id.ac.ui.cs.advprog.account.dto.delete;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeleteResponseDTO(
        @NotBlank
        @NotNull
        String status,

        @NotBlank
        @NotNull
        String message
) {
}
