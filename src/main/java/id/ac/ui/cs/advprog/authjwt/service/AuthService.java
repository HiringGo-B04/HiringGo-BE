package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.authjwt.facade.AuthenticationFacade;
import id.ac.ui.cs.advprog.authjwt.model.Token;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.TokenRepository;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.authjwt.service.command.AdminRegistrationCommand;
import id.ac.ui.cs.advprog.authjwt.service.command.LecturerRegistrationCommand;
import id.ac.ui.cs.advprog.authjwt.service.command.RegistrationCommand;
import id.ac.ui.cs.advprog.authjwt.service.command.StudentRegistrationCommand;
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
    private PasswordEncoder passwordEncoder;

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
    public ResponseEntity<Map<String, String>> register(@RequestBody User user, String role) {
        try{
            if(role == null || role.isEmpty()) {
                throw new IllegalArgumentException("Role is empty");
            }

            RegistrationCommand resgistrand;
            if(role.equalsIgnoreCase("admin")) {
                resgistrand = new AdminRegistrationCommand(userRepository, encoder, user);
            }
            else if(role.equalsIgnoreCase("lecturer")) {
                resgistrand = new LecturerRegistrationCommand(userRepository, encoder, user);
            }
            else if(role.equalsIgnoreCase("student")) {
                resgistrand = new StudentRegistrationCommand(userRepository, encoder, user);
            }
            else {
                throw new IllegalArgumentException("Role is invalid");
            }

            return resgistrand.addUser();
        }
        catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("messages", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.valueOf(401));
        }

    }
}