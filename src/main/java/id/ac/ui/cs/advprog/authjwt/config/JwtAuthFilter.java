package id.ac.ui.cs.advprog.authjwt.config;

import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secretKey;

    private JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = header.substring(7);
        if(!jwtUtil.validateJwtToken(token)) {
            chain.doFilter(req, res);
            return;
        }

        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_"+role));

        var auth = new UsernamePasswordAuthenticationToken(
                username,
                null,
                authorities
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(req, res);
    }
}
