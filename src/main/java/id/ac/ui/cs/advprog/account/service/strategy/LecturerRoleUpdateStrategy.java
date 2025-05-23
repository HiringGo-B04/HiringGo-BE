package id.ac.ui.cs.advprog.account.service.strategy;

import id.ac.ui.cs.advprog.account.dto.update.ResponseUpdateDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserIntoLecturerDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserUpdateDTO;
import id.ac.ui.cs.advprog.authjwt.config.GeneralUtils;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.model.UserRole;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("LECTURER")
public class LecturerRoleUpdateStrategy implements RoleUpdateStrategy {

    private final UserRepository userRepository;

    public LecturerRoleUpdateStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseUpdateDTO> updateRole(UserUpdateDTO updateData, User user) {
        UserIntoLecturerDTO userIntoLecturerDTO = (UserIntoLecturerDTO) updateData;

        if(userRepository.existsByNip(userIntoLecturerDTO.nip)){
            throw new IllegalArgumentException("NIP with this lecturer already exists");
        }

        if(!GeneralUtils.isValidInt(userIntoLecturerDTO.nip)){
            throw new IllegalArgumentException("NIM/NIP must only contain number and maximal 12 digits long");
        }

        if(!GeneralUtils.isValidString(userIntoLecturerDTO.fullName)){
            throw new IllegalArgumentException("Full name must only contain letters");
        }

        user.setNip(userIntoLecturerDTO.nip);
        user.setFullName(userIntoLecturerDTO.fullName);
        user.setNim(null);
        user.setRole(UserRole.LECTURER.getValue());

        try{
            userRepository.save(user);
            return new ResponseEntity<>(
                    new ResponseUpdateDTO("accept", "User updated to LECTURER"),
                    HttpStatus.OK
            );
        }
        catch(Exception e){
            return new ResponseEntity<>(
                    new ResponseUpdateDTO("error", e.getMessage()),
                    HttpStatus.valueOf(400));
        }
    }
}

