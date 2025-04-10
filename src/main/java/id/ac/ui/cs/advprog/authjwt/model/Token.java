package id.ac.ui.cs.advprog.authjwt.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "active_token")
@Getter @Setter
public class Token {
    @Id
    private String token;

    public Token() {}

    public Token(String token) {
        this.token = token;
    }
}