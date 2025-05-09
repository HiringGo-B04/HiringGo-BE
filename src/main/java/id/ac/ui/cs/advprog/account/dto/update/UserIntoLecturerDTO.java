package id.ac.ui.cs.advprog.account.dto.update;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserIntoLecturerDTO(
        @NotBlank
        @NotNull
        String role,

        @NotBlank
        @NotNull
        String fullName,

        @NotBlank
        @NotNull
        String nip

) implements UserUpdateDTO {}
