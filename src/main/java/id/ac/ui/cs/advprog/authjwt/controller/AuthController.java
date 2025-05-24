package id.ac.ui.cs.advprog.authjwt.controller;
import id.ac.ui.cs.advprog.authjwt.dto.login.LoginRequestDTO;
import id.ac.ui.cs.advprog.authjwt.dto.login.LoginResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.logout.LogoutRequestDTO;
import id.ac.ui.cs.advprog.authjwt.dto.logout.LogoutResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.AdminRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.LecturerRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.StudentRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.facade.AuthenticationFacade;
import id.ac.ui.cs.advprog.authjwt.model.UserRole;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    public static final String REGISTER_ADMIN = "/admin/signup";
    public static final String REGISTER_LECTURER = "/admin/signup/lecturer";
    public static final String REGISTER_STUDENT =  "/public/signup/student";
    public static final String LOGIN = "/public/signin";
    public static final String LOGOUT = "/user/logout";

    @Autowired
    private AuthenticationFacade authFacade;

    @PostMapping(LOGIN)
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO user) {
        return authFacade.login(user);
    }

    @PostMapping(REGISTER_ADMIN)
    public ResponseEntity<RegisterResponseDTO> registerAdmin(@Valid @RequestBody AdminRegistrationDTO user) {
        return authFacade.register(user, UserRole.ADMIN.getValue());
    }

    @PostMapping(REGISTER_STUDENT)
    public ResponseEntity<RegisterResponseDTO> registerStudent(@Valid @RequestBody StudentRegistrationDTO user) {
        return authFacade.register(user, UserRole.STUDENT.getValue());
    }

    @PostMapping(REGISTER_LECTURER)
    public ResponseEntity<RegisterResponseDTO> registerLecturer(@Valid @RequestBody LecturerRegistrationDTO user) {
        return authFacade.register(user, UserRole.LECTURER.getValue());
    }

    @PostMapping(LOGOUT)
    public ResponseEntity<LogoutResponseDTO> logoutUser(@Valid @RequestBody LogoutRequestDTO token) {
        return authFacade.logout(token);
    }
}