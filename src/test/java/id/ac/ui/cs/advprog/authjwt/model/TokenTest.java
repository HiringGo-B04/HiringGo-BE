package id.ac.ui.cs.advprog.authjwt.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenTest {

    @Test
    void testNoArgsConstructorAndSetterGetter() {
        // Given
        String sampleToken = "abc123xyz";

        // When
        Token token = new Token();     // no‑args constructor
        token.setToken(sampleToken);   // setter

        // Then
        assertThat(token.getToken())
                .as("getter should return the value set by the setter")
                .isEqualTo(sampleToken);
    }

    @Test
    void testParameterizedConstructor() {
        // Given
        String sampleToken = "token‑value‑456";

        // When
        Token token = new Token(sampleToken);

        // Then
        assertThat(token.getToken())
                .as("getter should return the value passed into the constructor")
                .isEqualTo(sampleToken);
    }
}
