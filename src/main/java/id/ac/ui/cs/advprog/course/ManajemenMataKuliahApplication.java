package id.ac.ui.cs.advprog.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@SpringBootApplication
public class ManajemenMataKuliahApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManajemenMataKuliahApplication.class, args);
    }

}
