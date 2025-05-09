package id.ac.ui.cs.advprog.account.dto.update;


import id.ac.ui.cs.advprog.account.dto.update.UserUpdateDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserIntoAdminDTO (
        @NotBlank
        @NotNull
        String role
) implements UserUpdateDTO {}
