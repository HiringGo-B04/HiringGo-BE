package id.ac.ui.cs.advprog.account.dto.update;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserIntoStudentDTO(
        @NotBlank
        @NotNull
        String role,

        @NotBlank
        @NotNull
        String fullName,

        @NotBlank
        @NotNull
        String nim

) implements UserUpdateDTO {}
