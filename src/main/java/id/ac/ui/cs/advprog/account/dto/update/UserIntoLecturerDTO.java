package id.ac.ui.cs.advprog.account.dto.update;

import jakarta.validation.constraints.NotBlank;

public class UserIntoLecturerDTO extends UserUpdateDTO {
    @NotBlank
    public String fullName;

    @NotBlank
    public String nip;
}

