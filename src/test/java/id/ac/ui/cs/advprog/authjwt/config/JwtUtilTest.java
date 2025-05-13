package id.ac.ui.cs.advprog.authjwt.config;

import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.authjwt.model.Token;
import id.ac.ui.cs.advprog.authjwt.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Component
public class JwtUtilTest {

    private JwtUtil jwtUtil;

    private TokenRepository tokenRespository;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "mySuperSecretKeyForJwtTesting1234567890");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 1000 * 60 * 60); // 1 hour
        jwtUtil.init();

        tokenRespository = mock(TokenRepository.class);
        ReflectionTestUtils.setField(jwtUtil, "tokenRepository", tokenRespository); // <-- this line is critical
    }

    @Test
    public void testGenerateTokenWithRole() {
        String token = jwtUtil.generateToken("testuser", "STUDENT");
        Token currentToken = new Token(token);

        assertNotNull(token);

        when(tokenRespository.findByToken(token)).thenReturn(currentToken);

        assertTrue(jwtUtil.validateJwtToken(token));

        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        assertEquals("testuser", username);
        assertEquals("STUDENT", role);
    }

    @Test
    public void testInvalidToken() {
        String invalidToken = "this.is.invalid.token";
        assertFalse(jwtUtil.validateJwtToken(invalidToken));
    }

    @Test
    public void testNotFoundToken() {
        String invalidToken = "notFoundToken";
        when(tokenRespository.findByToken(invalidToken)).thenReturn(null);
        assertFalse(jwtUtil.validateJwtToken(invalidToken));
    }

    @Test
    public void testExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 1); // 1ms for instant expiration
        jwtUtil.init();
        String token = jwtUtil.generateToken("testuser", "STUDENT");

        // wait to ensure it's expired
        Thread.sleep(5);

        assertFalse(jwtUtil.validateJwtToken(token));
    }
}
