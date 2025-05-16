package id.ac.ui.cs.advprog.authjwt.facade;

import id.ac.ui.cs.advprog.authjwt.dto.login.LoginRequestDTO;
import id.ac.ui.cs.advprog.authjwt.dto.login.LoginResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.logout.LogoutRequestDTO;
import id.ac.ui.cs.advprog.authjwt.dto.logout.LogoutResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.UserDTO;
import org.springframework.http.ResponseEntity;

public interface AuthenticationFacade {
    ResponseEntity<LoginResponseDTO> login(LoginRequestDTO user);
    ResponseEntity<RegisterResponseDTO> register(UserDTO user, String role);
    ResponseEntity<LogoutResponseDTO> logout(LogoutRequestDTO token);
}
