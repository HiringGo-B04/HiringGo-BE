package id.ac.ui.cs.advprog.authjwt.service.command;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

public abstract class RegistrationCommand {
    public UserRepository userRepository;
    public PasswordEncoder passwordEncoder;
    public User user;

    RegistrationCommand(UserRepository userRepository, PasswordEncoder passwordEncoder, User user) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.user = user;
    }

    public abstract ResponseEntity<Map<String, String>> addUser();
}