package id.ac.ui.cs.advprog.authjwt.controller;
import id.ac.ui.cs.advprog.authjwt.facade.AuthenticationFacade;
import id.ac.ui.cs.advprog.authjwt.model.Token;
import id.ac.ui.cs.advprog.authjwt.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationFacade authFacade;

    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        return authFacade.login(user);
    }

    @PostMapping("/signup/admin")
    public ResponseEntity<Map<String, String>> registerAdmin(@RequestBody User user) {
        return authFacade.register(user, "ADMIN");
    }

    @PostMapping("/signup/student")
    public ResponseEntity<Map<String, String>> registerStudent(@RequestBody User user) {
        return authFacade.register(user, "STUDENT");
    }
    @PostMapping("/signup/lecturer")
    public ResponseEntity<Map<String, String>> registerLecturer(@RequestBody User user) {
        return authFacade.register(user, "LECTURER");
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Token token) {
        return authFacade.logout(token);
    }
}