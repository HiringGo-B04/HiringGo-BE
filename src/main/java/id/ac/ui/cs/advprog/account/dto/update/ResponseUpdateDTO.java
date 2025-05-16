package id.ac.ui.cs.advprog.account.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResponseUpdateDTO(
        @NotBlank
        @NotNull
        String status,

        @NotBlank
        @NotNull
        String message
) { }
