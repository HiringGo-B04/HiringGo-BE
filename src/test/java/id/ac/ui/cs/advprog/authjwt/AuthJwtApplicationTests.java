package id.ac.ui.cs.advprog.authjwt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class AuthJwtApplicationTests {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> AuthJwtApplication.main(new String[] {}));
    }

}
