public record StudentDto(
        UUID userId,
        String username,
        String fullName,
        String role,
        String nim
) implements UserDto {}
