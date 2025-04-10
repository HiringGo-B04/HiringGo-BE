package id.ac.ui.cs.advprog.authjwt.facade;

import id.ac.ui.cs.advprog.authjwt.model.Token;
import id.ac.ui.cs.advprog.authjwt.model.User;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthenticationFacade {
    ResponseEntity<Map<String, String>> login(User user);
    ResponseEntity<Map<String, String>> register(User user);
    ResponseEntity<Map<String, String>> logout(Token token);
}
