package id.ac.ui.cs.advprog.authjwt.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Setter;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {

    @Id
    private UUID userId;
    private String username; // pass as email
    private String password;

    private String role;
    private String nip;
    private String nim;

    private String fullName;

    public User() {
        // Required by JPA
    }

    public User(String email, String password) {
        this.userId = UUID.randomUUID();
        this.username = email;
        this.password = password;
        this.role = "ADMIN";
    }
}