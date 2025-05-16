package id.ac.ui.cs.advprog.authjwt.dto.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO (
        @NotBlank
        @NotNull
        String username,

        @NotBlank
        @NotNull
        String password
) {}
