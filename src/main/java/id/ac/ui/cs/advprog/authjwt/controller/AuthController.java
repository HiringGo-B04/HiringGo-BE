package id.ac.ui.cs.advprog.authjwt.controller;
import id.ac.ui.cs.advprog.authjwt.dto.registration.AdminRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.LecturerRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.StudentRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.facade.AuthenticationFacade;
import id.ac.ui.cs.advprog.authjwt.model.Token;
import id.ac.ui.cs.advprog.authjwt.model.User;
import jakarta.validation.Valid;
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

//    @PostMapping("/public/signin")
//    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
//        return authFacade.login(user);
//    }

    @PostMapping("/admin/signup")
    public ResponseEntity<RegisterResponseDTO> registerAdmin(@Valid @RequestBody AdminRegistrationDTO user) {
        return authFacade.register(user, "ADMIN");
    }

    @PostMapping("/public/signup/student")
    public ResponseEntity<RegisterResponseDTO> registerStudent(@Valid @RequestBody StudentRegistrationDTO user) {
        return authFacade.register(user, "STUDENT");
    }

    @PostMapping("/admin/signup/lecturer")
    public ResponseEntity<RegisterResponseDTO> registerLecturer(@Valid @RequestBody LecturerRegistrationDTO user) {
        return authFacade.register(user, "LECTURER");
    }

    @PostMapping("/user/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Token token) {
        return authFacade.logout(token);
    }
}