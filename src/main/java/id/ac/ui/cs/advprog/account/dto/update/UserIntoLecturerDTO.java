package id.ac.ui.cs.advprog.account.dto.update;

import jakarta.validation.constraints.NotBlank;

public class UserIntoLecturerDTO extends UserUpdateDTO {
    @NotBlank
    String fullName;

    @NotBlank
    String nip;
}

