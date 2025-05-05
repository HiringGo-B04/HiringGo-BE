package id.ac.ui.cs.advprog.authjwt.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "mySuperSecretKeyForJwtTesting1234567890"); // min 32 chars
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 1000 * 60 * 60); // 1 hour
        jwtUtil.init();
    }

    @Test
    public void testGenerateTokenWithRole() {
        String token = jwtUtil.generateToken("testuser", "STUDENT");

        assertNotNull(token);
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
    public void testExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 1); // 1ms for instant expiration
        jwtUtil.init();
        String token = jwtUtil.generateToken("testuser", "STUDENT");

        // wait to ensure it's expired
        Thread.sleep(5);

        assertFalse(jwtUtil.validateJwtToken(token));
    }
}
