package id.ac.ui.cs.advprog.authjwt.service.command;

import id.ac.ui.cs.advprog.authjwt.dto.registration.LecturerRegistrationDTO;
import id.ac.ui.cs.advprog.authjwt.dto.registration.RegisterResponseDTO;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.model.UserFactory;
import id.ac.ui.cs.advprog.authjwt.model.UserRole;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static id.ac.ui.cs.advprog.authjwt.config.GeneralUtils.*;

public class LecturerRegistrationCommand extends RegistrationCommand {
    public LecturerRegistrationCommand(UserRepository userRepository, PasswordEncoder passwordEncoder, LecturerRegistrationDTO user) {
        super(userRepository,passwordEncoder,user);
    }

    @Override
    @Transactional
    public ResponseEntity<RegisterResponseDTO> addUser() {
        LecturerRegistrationDTO lecturer = (LecturerRegistrationDTO) user;

        Map<String, String> validity = check_invalid_input("lecturer");
        if(!"valid".equals(validity.get(DEFAULT_MESSAGE_RESPONSE))) {
            return new ResponseEntity<>(
                    new RegisterResponseDTO(DEFAULT_ERROR_RESPONSE, validity.get(DEFAULT_MESSAGE_RESPONSE)),
                    HttpStatus.valueOf(400));
        }

        if(userRepository.existsByNip(lecturer.nip())) {
            return new ResponseEntity<>(
                    new RegisterResponseDTO(DEFAULT_ERROR_RESPONSE, "NIP Already exists"),
                    HttpStatus.valueOf(400));
        }

        try{
            User newUser = UserFactory.createLecturer(
                    UUID.randomUUID(),
                    user.username(),
                    passwordEncoder.encode(user.password()),
                    ((LecturerRegistrationDTO) user).fullName(),
                    ((LecturerRegistrationDTO) user).nip()
            );

            userRepository.save(newUser);

            return new ResponseEntity<>(
                    new RegisterResponseDTO(
                            DEFAULT_ACCEPT_RESPONSE,
                            "Success register",
                            newUser.getUsername(),
                            UserRole.LECTURER.getValue()),
                    HttpStatus.valueOf(200));
        }
        catch (Exception e) {
            return new ResponseEntity<>(
                    new RegisterResponseDTO(DEFAULT_ERROR_RESPONSE, e.getMessage()),
                    HttpStatus.valueOf(400));
        }
    }

}