package id.ac.ui.cs.advprog.authjwt.service.command;
import id.ac.ui.cs.advprog.authjwt.config.GeneralUtils;
import id.ac.ui.cs.advprog.authjwt.dto.*;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

public abstract class RegistrationCommand {
    public UserRepository userRepository;
    public PasswordEncoder passwordEncoder;
    public UserDTO user;

    public Map<String, String> check_invalid_input(String status) {
        Map<String, String> response = new HashMap<>();

        if(!GeneralUtils.isValidEmail(user.username())) {
            response.put("message", "Username must be a valid email address");
            return response;
        }

        if(userRepository.existsByUsername(user.username())) {
            response.put("message", "Username already exists");
            return response;
        }

        if(!status.equals("admin")){
            String idNumber = null;
            String fullName = "";

            if ("student".equals(status) && user instanceof StudentRegistrationDTO student) {
                idNumber = student.nim();
                fullName = student.fullName();
            } else if ("lecturer".equals(status) && user instanceof LecturerRegistrationDTO lecturer) {
                idNumber = lecturer.nip();
                fullName = lecturer.fullName();
            }

            if(!GeneralUtils.isValidString(fullName)) {
                response.put("message", "Name must only contain letter character");
                return response;
            }

            if (!GeneralUtils.isValidInt(idNumber)) {
                response.put("message", "NIM/NIP must only contain number and maximal 12 digits long");
                return response;
            }
        }

        response.put("message", "valid");
        return response;
    }

    protected RegistrationCommand(UserRepository userRepository, PasswordEncoder passwordEncoder, UserDTO user) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.user = user;
    }

    public abstract ResponseEntity<RegisterResponseDTO> addUser();
}