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

    public User(UUID uuid, String email, String password) {
        this.userId = uuid;
        this.username = email;
        this.password = password;
        this.role = "ADMIN";
    }

    public User(UUID uuid, String email, String password, String fullName, boolean worker, String number) {
        this.userId = uuid;
        this.username = email;
        this.password = password;
        this.fullName = fullName;
        if(worker) {
            this.role = "LECTURER";
            this.nip = number;
        }
        else{
            this.role = "STUDENT";
            this.nim = number;
        }
    }
}