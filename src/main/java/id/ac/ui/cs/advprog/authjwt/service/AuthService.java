package id.ac.ui.cs.advprog.authjwt.service;

import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.authjwt.dto.login.LoginRequestDTO;
import id.ac.ui.cs.advprog.authjwt.dto.login.LoginResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.logout.LogoutRequestDTO;
import id.ac.ui.cs.advprog.authjwt.dto.logout.LogoutResponseDTO;
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

    private final String defaultAcceptResponse = "accept";
    private final String defaultErrorResponse = "error";

    @Override
    @Transactional
    public ResponseEntity<LoginResponseDTO> login(LoginRequestDTO user){
        User exist_user = userRepository.findByUsername(user.username());

        if(exist_user == null) {
            return new ResponseEntity<>(
                    new LoginResponseDTO(defaultErrorResponse, "User didn't exist"),
                    HttpStatus.valueOf(400));
        }

        if (!encoder.matches(user.password(), exist_user.getPassword())) {
            return new ResponseEntity<>(
                    new LoginResponseDTO(defaultErrorResponse, "Invalid Password"),
                    HttpStatus.valueOf(400));
        }

        try{
            String jwt_token = jwtUtils.generateToken(user.username(), exist_user.getRole(), exist_user.getUserId());
            Token user_token = new Token(jwt_token);
            tokenRepository.save(user_token);

            return new ResponseEntity<>(
                    new LoginResponseDTO(defaultAcceptResponse, "Success login", jwt_token),
                    HttpStatus.valueOf(200));
        }
        catch (Exception e) {
            return new ResponseEntity<>(
                    new LoginResponseDTO("error on prod", e.getMessage()),
                    HttpStatus.valueOf(400));
        }
    }

    @Transactional
    @Override
    public ResponseEntity<LogoutResponseDTO> logout(LogoutRequestDTO token){
        try{

            if(tokenRepository.findByToken(token.token()) == null) {
                throw new Exception("Token not found");
            }

            tokenRepository.deleteByToken(token.token());
            return new ResponseEntity<>(
                    new LogoutResponseDTO(defaultAcceptResponse, "Succes to logout"),
                    HttpStatus.valueOf(200));
        }
        catch (Exception e) {
            return new ResponseEntity<>(
                    new LogoutResponseDTO(defaultErrorResponse, e.getMessage()),
                    HttpStatus.valueOf(400));
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
                    new RegisterResponseDTO(defaultErrorResponse,e.getMessage()),
                    HttpStatus.valueOf(400));
        }

    }
}