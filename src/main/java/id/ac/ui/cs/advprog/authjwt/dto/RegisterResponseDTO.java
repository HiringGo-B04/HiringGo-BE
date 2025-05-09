package id.ac.ui.cs.advprog.authjwt.dto;

public record RegisterResponseDTO (
        String status,
        String messages,
        String username,
        String role
) {
    // Overloaded constructor that accepts only status and messages
    public RegisterResponseDTO(String status, String messages) {
        this(status, messages, null, null);  // Default username and role to null
    }
}
