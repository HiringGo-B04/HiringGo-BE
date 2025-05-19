package id.ac.ui.cs.advprog.account.dto.get;

import id.ac.ui.cs.advprog.authjwt.model.User;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record GetAllUserDTO(
        @NotBlank
        String status,

        @NotBlank
        String message,

        List<User> users
) { }
