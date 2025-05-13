package id.ac.ui.cs.advprog.authjwt.testconfig;

import id.ac.ui.cs.advprog.authjwt.config.JwtAuthFilter;
import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.authjwt.repository.TokenRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestSecurityBeansConfig {

    @Bean
    public TokenRepository tokenRepository() {
        return mock(TokenRepository.class);
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtUtil jwtUtil) {
        return new JwtAuthFilter(jwtUtil);
    }
}
