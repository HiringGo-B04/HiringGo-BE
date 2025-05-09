package id.ac.ui.cs.advprog.authjwt.service.command;

import id.ac.ui.cs.advprog.authjwt.dto.AdminRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class AdminRegistrationCommand extends RegistrationCommand {
    public AdminRegistrationCommand(UserRepository userRepository, PasswordEncoder passwordEncoder, AdminRegistrationDTO user) {
        super(userRepository,passwordEncoder,user);
    }

    @Override
    @Transactional
    public ResponseEntity<RegisterResponseDTO> addUser() {
//        Map<String, String> response = new HashMap<>();
        Map<String, String> validity = check_invalid_input("admin");

//        if(user.getPassword() == null || user.getPassword().isEmpty() || user.getUsername() == null || user.getUsername().isEmpty()) {
//            response.put("status", "error");
//            response.put("message", "Invalid payload");
//            return new ResponseEntity<>(response, HttpStatus.valueOf(403));
//        }

        if(!"valid".equals(validity.get("message"))) {
//            response.put("status", "error");
//            response.put("message", validity.get("message"));
            return new ResponseEntity<>(
                    new RegisterResponseDTO("error", validity.get("message")),
                    HttpStatus.valueOf(Integer.parseInt(validity.get("code"))));
//            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(validity.get("code"))));
        }


        try{
            User newUser = new User(
                    UUID.randomUUID(),
                    user.username(),
                    passwordEncoder.encode(user.password())
            );

            userRepository.save(newUser);

//            response.put("status", "accept");
//            response.put("messages", "Success register");
//            response.put("username", newUser.getUsername());
//            response.put("role", "ADMIN");

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
//            response.put("status", "error");
//            response.put("messages", e.getMessage());
            return new ResponseEntity<>(new RegisterResponseDTO("error", e.getMessage()), HttpStatus.valueOf(401));
        }
    }
}