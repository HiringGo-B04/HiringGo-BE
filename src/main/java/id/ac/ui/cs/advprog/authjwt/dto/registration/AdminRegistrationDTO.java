package id.ac.ui.cs.advprog.authjwt.dto.registration;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminRegistrationDTO (
        @NotBlank
        @NotNull
        String username,

        @NotBlank
        @NotNull
        String password
) implements UserDTO {}
