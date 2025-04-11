package id.ac.ui.cs.advprog.course.config;

import id.ac.ui.cs.advprog.course.repository.InMemoryMataKuliahRepository;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MataKuliahRepository mataKuliahRepository() {
        return new InMemoryMataKuliahRepository();
    }
}

