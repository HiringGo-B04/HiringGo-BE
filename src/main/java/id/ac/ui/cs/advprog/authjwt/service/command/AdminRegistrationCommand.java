package id.ac.ui.cs.advprog.authjwt.service.command;

import id.ac.ui.cs.advprog.authjwt.dto.registration.AdminRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;


public class AdminRegistrationCommand extends RegistrationCommand {
    public AdminRegistrationCommand(UserRepository userRepository, PasswordEncoder passwordEncoder, AdminRegistrationDTO user) {
        super(userRepository,passwordEncoder,user);
    }

    @Override
    @Transactional
    public ResponseEntity<RegisterResponseDTO> addUser() {
        Map<String, String> validity = check_invalid_input("admin");

        if(!"valid".equals(validity.get("message"))) {
            return new ResponseEntity<>(
                    new RegisterResponseDTO("error", validity.get("message")),
                    HttpStatus.valueOf(400));
        }

        try{
            User newUser = new User(
                    UUID.randomUUID(),
                    user.username(),
                    passwordEncoder.encode(user.password())
            );

            userRepository.save(newUser);

            return new ResponseEntity<>(
                    new RegisterResponseDTO(
                            "accept",
                            "Success register",
                            newUser.getUsername(),
                            "ADMIN"
                    ),
                    HttpStatus.valueOf(200));
        }
        catch (Exception e) {
            return new ResponseEntity<>(new RegisterResponseDTO("error", e.getMessage()), HttpStatus.valueOf(401));
        }
    }
}