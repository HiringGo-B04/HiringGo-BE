package id.ac.ui.cs.advprog.authjwt.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

public class SecurityConfigTest {
    @Test
    void testPasswordEncoderMethodDirectly() {
        SecurityConfig config = new SecurityConfig(); // Manual instantiation
        PasswordEncoder encoder = config.passwordEncoder(); // Direct method call

        String rawPassword = "password123";
        String encoded = encoder.encode(rawPassword);

        assertThat(encoded).isNotEqualTo(rawPassword);
        assertThat(encoder.matches(rawPassword, encoded)).isTrue();
    }
}
