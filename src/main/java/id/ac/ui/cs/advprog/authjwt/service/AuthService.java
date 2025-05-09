package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.authjwt.dto.login.LoginRequestDTO;
import id.ac.ui.cs.advprog.authjwt.dto.login.LoginResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.*;
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

import java.util.HashMap;
import java.util.Map;

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
    @Transactional
    public ResponseEntity<LoginResponseDTO> login(LoginRequestDTO user){
        User exist_user = userRepository.findByUsername(user.username());

        if(exist_user == null) {
            return new ResponseEntity<>(
                    new LoginResponseDTO("error", "User not found"),
                    HttpStatus.valueOf(400));
        }

        if (!encoder.matches(user.password(), exist_user.getPassword())) {
            return new ResponseEntity<>(
                    new LoginResponseDTO("error", "Invalid Password"),
                    HttpStatus.valueOf(400));
        }

        try{
            String jwt_token = jwtUtils.generateToken(user.username(), exist_user.getRole());
            Token user_token = new Token(jwt_token);
            tokenRepository.save(user_token);

            return new ResponseEntity<>(
                    new LoginResponseDTO("accept", "Success login", jwt_token),
                    HttpStatus.valueOf(200));
        }
        catch (Exception e) {
            return new ResponseEntity<>(
                    new LoginResponseDTO("error", e.getMessage()),
                    HttpStatus.valueOf(400));
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
    public ResponseEntity<RegisterResponseDTO> register(UserDTO user, String role) {
        try{
            if(role == null || role.isEmpty()) {
                throw new IllegalArgumentException("Role is empty");
            }

            RegistrationCommand resgistrand;
            if(role.equalsIgnoreCase("admin")) {
                resgistrand = new AdminRegistrationCommand(userRepository, encoder, (AdminRegistrationDTO) user);
            }
            else if(role.equalsIgnoreCase("lecturer")) {
                resgistrand = new LecturerRegistrationCommand(userRepository, encoder, (LecturerRegistrationDTO) user);
            }
            else if(role.equalsIgnoreCase("student")) {
                resgistrand = new StudentRegistrationCommand(userRepository, encoder, (StudentRegistrationDTO) user);
            }
            else {
                throw new IllegalArgumentException("Role is invalid");
            }

            return resgistrand.addUser();
        }
        catch (Exception e) {
            return new ResponseEntity<>(
                    new RegisterResponseDTO("error",e.getMessage()),
                    HttpStatus.valueOf(400));
        }

    }
}