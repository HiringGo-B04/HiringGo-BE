package id.ac.ui.cs.advprog.authjwt.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TokenTest {

    @Test
    void testTokenGettersAndSetters() {
        // Create a Token object
        Token token = new Token();

        // Set values using setters
        token.setToken("sampleToken123");

        // Assertions to check if the getters return the expected values
        assertThat(token.getToken()).isEqualTo("sampleToken123");
    }

    @Test
    void testTokenConstructor() {
        // Create a Token object using the constructor
        Token token = new Token("sampleToken456");

        // Assertions to check if the constructor sets the token value correctly
        assertThat(token.getToken()).isEqualTo("sampleToken456");
    }
}
