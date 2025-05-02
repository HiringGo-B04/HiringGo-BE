package id.ac.ui.cs.advprog.authjwt.service.command;
import id.ac.ui.cs.advprog.authjwt.config.GeneralUtils;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

public abstract class RegistrationCommand {
    public UserRepository userRepository;
    public PasswordEncoder passwordEncoder;
    public User user;

    public Map<String, String> check_invalid_input(String username, String number) {
        Map<String, String> response = new HashMap<>();
        if(userRepository.existsByUsername(username)) {
            response.put("code", "404");
            response.put("message", "Username already exists");
            return response;
        }

        if(!GeneralUtils.isValidEmail(username)) {
            response.put("code", "403");
            response.put("message", "Username must be a valid email address");
            return response;
        }

        System.out.println(number);
        if(!GeneralUtils.isValidInt(number)){
            response.put("code", "403");
            response.put("message", "NIM/NIP must only contain number and maximal 12 digits long");
            return response;
        }

        response.put("message", "valid");
        return response;
    }

    RegistrationCommand(UserRepository userRepository, PasswordEncoder passwordEncoder, User user) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.user = user;
    }

    public abstract ResponseEntity<Map<String, String>> addUser();
}