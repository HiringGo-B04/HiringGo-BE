package id.ac.ui.cs.advprog.authjwt.facade;

import id.ac.ui.cs.advprog.authjwt.dto.registration.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.UserDTO;
import id.ac.ui.cs.advprog.authjwt.model.Token;
import id.ac.ui.cs.advprog.authjwt.model.User;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthenticationFacade {
    ResponseEntity<Map<String, String>> login(User user);
    ResponseEntity<RegisterResponseDTO> register(UserDTO user, String role);
    ResponseEntity<Map<String, String>> logout(Token token);
}
