package id.ac.ui.cs.advprog.account.dto.update;

import jakarta.validation.constraints.NotBlank;

public class UserIntoStudentDTO extends UserUpdateDTO {
    @NotBlank
    public String fullName;

    @NotBlank
    public String nim;

    public UserIntoStudentDTO(String fullName, String nim, String username, String role) {
        super(role, username);
        this.fullName = fullName;
        this.nim = nim;
    }
}