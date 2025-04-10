package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.authjwt.facade.AuthenticationFacade;
import id.ac.ui.cs.advprog.authjwt.model.Token;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.TokenRepository;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService implements AuthenticationFacade {
    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtil jwtUtils;

    @Override
    public ResponseEntity<Map<String, String>> login(User user){
        User exist_user = userRepository.findByUsername(user.getUsername());
        Map<String, String> response = new HashMap<>();

        if(exist_user == null) {
            response.put("status", "error");
            response.put("messages", "User not found");
            return new ResponseEntity<>(response, HttpStatus.valueOf(404));
        }

        if (!encoder.matches(user.getPassword(), exist_user.getPassword())) {
            response.put("status", "error");
            response.put("messages", "Invalid password");
            return new ResponseEntity<>(response, HttpStatus.valueOf(404));
        }

        try{
            String jwt_token = jwtUtils.generateToken(user.getUsername());
            Token user_token = new Token(jwt_token);
            tokenRepository.save(user_token);

            response.put("status", "accept");
            response.put("messages", "Success login");
            response.put("token", jwt_token);
            return new ResponseEntity<>(response, HttpStatus.valueOf(200));
        }
        catch (Exception e) {
            response.put("status", "error");
            response.put("messages", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.valueOf(401));
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Map<String, String>> logout(Token token){
        Map<String, String> response = new HashMap<>();
        try{
            if(tokenRepository.findByToken(token.getToken()) == null) {
                throw new IllegalArgumentException("Invalid Token");
            }

            tokenRepository.deleteByToken(token.getToken());

            response.put("status", "accept");
            response.put("messages", "Success to logout");
            return new ResponseEntity<>(response, HttpStatus.valueOf(200));
        }
        catch (Exception e) {
            response.put("status", "error");
            response.put("messages", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.valueOf(401));
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        if (userRepository.existsByUsername(user.getUsername())) {
            response.put("status", "error");
            response.put("messages", "Username already exists");
            return new ResponseEntity<>(response, HttpStatus.valueOf(404));
        }

        try{
            User newUser = new User(
                    UUID.randomUUID(),
                    user.getUsername(),
                    encoder.encode(user.getPassword())
            );
            userRepository.save(newUser);
            response.put("status", "accept");
            response.put("messages", "Success register");
            response.put("username", newUser.getUsername());
            response.put("role", newUser.getRole());
            return new ResponseEntity<>(response, HttpStatus.valueOf(200));
        }
        catch (Exception e) {
            response.put("status", "error");
            response.put("messages", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.valueOf(401));
        }
    }
}