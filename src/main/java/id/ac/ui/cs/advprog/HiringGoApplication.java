package id.ac.ui.cs.advprog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "id.ac.ui.cs.advprog.course",
        "id.ac.ui.cs.advprog.log",
        "id.ac.ui.cs.advprog.manajemenlowongan",
        "id.ac.ui.cs.advprog.mendaftarlowongan",
        "id.ac.ui.cs.advprog.authjwt"

})

public class HiringGoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HiringGoApplication.class, args);
    }

}