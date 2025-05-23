package id.ac.ui.cs.advprog.account.dto.update;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "role",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserIntoAdminDTO.class, name = "ADMIN"),
        @JsonSubTypes.Type(value = UserIntoLecturerDTO.class, name = "LECTURER"),
        @JsonSubTypes.Type(value = UserIntoStudentDTO.class, name = "STUDENT"),
})
public abstract class UserUpdateDTO {
    @NotBlank
    public String role;

    @NotBlank
    public String username;

    public UserUpdateDTO(String role, String username) {
        this.role = role;
        this.username = username;
    }
}
