package id.ac.ui.cs.advprog.mendaftarlowongan.model;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class User {
    private UUID id;

    private String email;
    private String password;

    public User() {
        this.id = UUID.randomUUID();
    }

    public User(String email, String password) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.password = password;
    }
}

