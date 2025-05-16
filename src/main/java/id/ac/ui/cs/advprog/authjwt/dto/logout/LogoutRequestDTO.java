package id.ac.ui.cs.advprog.authjwt.dto.logout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LogoutRequestDTO (
        @NotBlank
        @NotNull
        String token
) {}
