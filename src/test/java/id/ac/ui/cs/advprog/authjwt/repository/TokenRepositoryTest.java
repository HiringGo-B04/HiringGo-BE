package id.ac.ui.cs.advprog.authjwt.repository;

import id.ac.ui.cs.advprog.authjwt.model.Token;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Test
    @DisplayName("findByToken returns the saved Token")
    void testFindByTokenReturnsEntity() {
        Token saved = tokenRepository.save(new Token("abc123"));
        Token found = tokenRepository.findByToken("abc123");
        assertThat(found)
                .as("Should find the token we just saved")
                .isNotNull();
        assertThat(found.getToken())
                .isEqualTo(saved.getToken());
    }

    @Test
    void testFindByTokenReturnsNullWhenNotExists() {
        Token found = tokenRepository.findByToken("does-not-exist");
        assertThat(found)
                .as("No token should be found for a random value")
                .isNull();
    }

    @Test
    public void testDeleteByToken_successfulDeletion() {
        // Arrange
        String token = "validToken";
        tokenRepository.save(new Token("abc123"));
        tokenRepository.deleteByToken(token);

        Token found = tokenRepository.findByToken(token);
        Assertions.assertNull(found);
    }

    @Test
    public void testDeleteByToken_tokenNotFound() {
        // Arrange
        String token = "validToken";
        tokenRepository.save(new Token("abc123"));
        tokenRepository.deleteByToken(token);

        Token found = tokenRepository.findByToken("abc123");
        Assertions.assertNotNull(found);
    }

}
