package id.ac.ui.cs.advprog.authjwt.facade;

import id.ac.ui.cs.advprog.authjwt.dto.AdminRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.UserDTO;
import id.ac.ui.cs.advprog.authjwt.model.Token;
import id.ac.ui.cs.advprog.authjwt.model.User;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthenticationFacade {
    ResponseEntity<Map<String, String>> login(User user);
    ResponseEntity<RegisterResponseDTO> register(UserDTO user, String role);
    ResponseEntity<Map<String, String>> logout(Token token);
}
