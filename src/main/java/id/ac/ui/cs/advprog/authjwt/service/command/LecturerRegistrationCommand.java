package id.ac.ui.cs.advprog.authjwt.service.command;

import id.ac.ui.cs.advprog.authjwt.dto.registration.LecturerRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.model.UserRole;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

public class LecturerRegistrationCommand extends RegistrationCommand {
    public LecturerRegistrationCommand(UserRepository userRepository, PasswordEncoder passwordEncoder, LecturerRegistrationDTO user) {
        super(userRepository,passwordEncoder,user);
    }

    @Override
    @Transactional
    public ResponseEntity<RegisterResponseDTO> addUser() {
        LecturerRegistrationDTO lecturer = (LecturerRegistrationDTO) user;

        Map<String, String> validity = check_invalid_input("lecturer");
        if(!"valid".equals(validity.get(defaultMessageResponse))) {
            return new ResponseEntity<>(
                    new RegisterResponseDTO(defaultErrorResponse, validity.get(defaultMessageResponse)),
                    HttpStatus.valueOf(400));
        }

        if(userRepository.existsByNip(lecturer.nip())) {
            return new ResponseEntity<>(
                    new RegisterResponseDTO(defaultErrorResponse, "NIP Already exists"),
                    HttpStatus.valueOf(400));
        }

        try{
            User newUser = new User(
                    UUID.randomUUID(),
                    user.username(),
                    passwordEncoder.encode(user.password()),
                    ((LecturerRegistrationDTO) user).fullName(),
                    true,
                    ((LecturerRegistrationDTO) user).nip()
            );

            userRepository.save(newUser);

            return new ResponseEntity<>(
                    new RegisterResponseDTO(
                            defaultAcceptResponse,
                            "Success register",
                            newUser.getUsername(),
                            UserRole.LECTURER.getValue()),
                    HttpStatus.valueOf(200));
        }
        catch (Exception e) {
            return new ResponseEntity<>(
                    new RegisterResponseDTO(defaultErrorResponse, e.getMessage()),
                    HttpStatus.valueOf(400));
        }
    }

}