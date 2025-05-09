package id.ac.ui.cs.advprog.authjwt.dto.login;

public record LoginResponseDTO (
        String status,
        String message,
        String token
) {
    public LoginResponseDTO(String status, String message) {
        this(status, message, null);
    }
}
