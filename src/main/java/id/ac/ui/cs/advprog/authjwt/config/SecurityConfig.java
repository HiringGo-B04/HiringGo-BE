package id.ac.ui.cs.advprog.authjwt.config;

import id.ac.ui.cs.advprog.authjwt.model.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;
    private final String ADMIN_ROLE = UserRole.ADMIN.name();
    private final String STUDENT_ROLE = UserRole.STUDENT.name();
    private final String LECTURER_ROLE = UserRole.LECTURER.name();

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtFilter = new JwtAuthFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        /*
                         * Jika ingin melakukan testing tanpa adanya RBAC comment
                         */
                        .requestMatchers("/api/auth/admin/**").hasRole(ADMIN_ROLE)
                        .requestMatchers("/api/auth/user/**").hasAnyRole(ADMIN_ROLE, STUDENT_ROLE, LECTURER_ROLE)

                        .requestMatchers("/api/account/admin/**").hasRole(ADMIN_ROLE)

                        .requestMatchers("/api/lamaran/student/**").hasRole(STUDENT_ROLE)
                        .requestMatchers("/api/lamaran/user/**").hasAnyRole(ADMIN_ROLE, STUDENT_ROLE, LECTURER_ROLE)
                        .requestMatchers("/api/lamaran/lecturer/**").hasRole(LECTURER_ROLE)
                        .requestMatchers("/api/log/student/**").hasRole(STUDENT_ROLE)
                        .requestMatchers("/api/log/lecturer/**").hasRole(LECTURER_ROLE)

                        // for debugging purpose
                        .requestMatchers("/api/lowongan/user/**").hasAnyRole(ADMIN_ROLE, STUDENT_ROLE, LECTURER_ROLE)
                        .requestMatchers("/api/lowongan/lecturer/**").hasRole(LECTURER_ROLE)
                        /*
                         * Hingga sini yang permitAll bawah ngga usah itu kayak namanya nge permit semuanya
                         */
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "UPDATE", "PUT", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}