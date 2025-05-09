package id.ac.ui.cs.advprog.authjwt.dto.registration;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LecturerRegistrationDTO(
        @NotBlank
        @NotNull
        String username,

        @NotBlank
        @NotNull
        String password,

        @NotBlank
        @NotNull
        String nip,

        @NotBlank
        @NotNull
        String fullName
) implements UserDTO {}
