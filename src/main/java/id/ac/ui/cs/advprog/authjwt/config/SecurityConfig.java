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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;
    private final String adminRole = UserRole.ADMIN.name();
    private final String studentRole = UserRole.STUDENT.name();
    private final String lecturerRole = UserRole.LECTURER.name();

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
                        .requestMatchers("/api/auth/admin/**").hasRole(adminRole)
                        .requestMatchers("/api/auth/user/**").hasAnyRole(adminRole, studentRole, lecturerRole)

                        .requestMatchers("/api/account/admin/**").hasRole(adminRole)

                        .requestMatchers("/api/lamaran/student/**").hasRole(studentRole)
                        .requestMatchers("/api/lamaran/user/**").hasAnyRole(adminRole, studentRole, lecturerRole)
                        .requestMatchers("/api/lamaran/lecturer/**").hasRole(lecturerRole)
                        .requestMatchers("/api/log/student/**").hasRole(studentRole)
                        .requestMatchers("/api/log/lecturer/**").hasRole(lecturerRole)

                        // for debugging purpose
//                        .requestMatchers("/api/lowongan/user/**").hasAnyRole(adminRole, studentRole, lecturerRole)
                        .requestMatchers("/api/lowongan/lecturer/**").hasRole(lecturerRole)
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