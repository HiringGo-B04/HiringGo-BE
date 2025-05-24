package id.ac.ui.cs.advprog.authjwt.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        JwtUtil jwtUtil = new JwtUtil(); // Use real instance if JwtUtil is stateless or mock-safe
        securityConfig = new SecurityConfig(jwtUtil);
    }

    @Test
    void testPasswordEncoderMethodDirectly() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        String rawPassword = "password123";
        String encoded = encoder.encode(rawPassword);

        assertThat(encoded).isNotEqualTo(rawPassword);
        assertThat(encoder.matches(rawPassword, encoded)).isTrue();
    }

    @Test
    void testCorsConfigurationSource() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        var config = source.getCorsConfiguration(new MockHttpServletRequest());

        assertThat(config).isNotNull();
        assertThat(config.getAllowedOrigins()).containsExactly("http://localhost:3000");
        assertThat(config.getAllowedMethods()).containsExactlyInAnyOrder("GET", "POST", "PUT", "DELETE", "PATCH", "UPDATE", "PUT", "OPTIONS");
        assertThat(config.getAllowedHeaders()).containsExactly("*");
        assertThat(config.getAllowCredentials()).isTrue();
    }
}
