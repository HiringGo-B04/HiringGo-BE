package id.ac.ui.cs.advprog.authjwt.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtAuthFilterTest {

    private JwtUtil jwtUtil;
    private JwtAuthFilter jwtAuthFilter;
    private FilterChain filterChain;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setUp() {
        jwtUtil = mock(JwtUtil.class);
        jwtAuthFilter = new JwtAuthFilter(jwtUtil);
        filterChain = mock(FilterChain.class);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();  // Clear context before each test
    }

    @Test
    public void testNoAuthorizationHeader() throws ServletException, IOException {
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testOptionsRequestBypassesFilter() throws ServletException, IOException {
        request.setMethod("OPTIONS");  // Simulate an OPTIONS request

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // It should immediately respond with SC_OK
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        // It should still pass the request down the filter chain
        verify(filterChain).doFilter(request, response);

        // No authentication should be set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }


    @Test
    public void testInvalidToken() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer invalidtoken");

        when(jwtUtil.validateJwtToken("invalidtoken")).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testValidToken() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer validtoken");

        when(jwtUtil.validateJwtToken("validtoken")).thenReturn(true);
        when(jwtUtil.getUsernameFromToken("validtoken")).thenReturn("testuser");
        when(jwtUtil.getRoleFromToken("validtoken")).thenReturn("ADMIN");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("testuser", authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        verify(filterChain).doFilter(request, response);
    }
}
