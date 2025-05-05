package id.ac.ui.cs.advprog.authjwt.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenTest {

    @Test
    void testNoArgsConstructorAndSetterGetter() {
        String sampleToken = "abc123xyz";

        Token token = new Token();
        token.setToken(sampleToken);

        assertThat(token.getToken())
                .as("getter should return the value set by the setter")
                .isEqualTo(sampleToken);
    }

    @Test
    void testParameterizedConstructor() {
        String sampleToken = "token‑value‑456";

        Token token = new Token(sampleToken);

        assertThat(token.getToken())
                .as("getter should return the value passed into the constructor")
                .isEqualTo(sampleToken);
    }
}
