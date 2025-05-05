package id.ac.ui.cs.advprog.course;

import id.ac.ui.cs.advprog.course.service.MataKuliahService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest(classes = {ManajemenMataKuliahApplication.class, ManajemenMataKuliahApplicationTests.TestConfig.class})
class ManajemenMataKuliahApplicationTests {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MataKuliahService mataKuliahService() {
            return Mockito.mock(MataKuliahService.class);
        }
    }

    @Test
    void contextLoads() {
    }
}
