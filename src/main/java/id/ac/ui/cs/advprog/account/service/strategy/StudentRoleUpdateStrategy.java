package id.ac.ui.cs.advprog.account.service.strategy;

import id.ac.ui.cs.advprog.account.dto.update.ResponseUpdateDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserIntoStudentDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserUpdateDTO;
import id.ac.ui.cs.advprog.authjwt.config.GeneralUtils;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.model.UserRole;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("STUDENT")
public class StudentRoleUpdateStrategy implements RoleUpdateStrategy {

    private final UserRepository userRepository;

    public StudentRoleUpdateStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseUpdateDTO> updateRole(UserUpdateDTO updateData, User user) {
        UserIntoStudentDTO userIntoStudentDTO = (UserIntoStudentDTO) updateData;

        if(userRepository.existsByNip(userIntoStudentDTO.nim)){
            throw new IllegalArgumentException("NIM with this student already exists");
        }

        if(!GeneralUtils.isValidInt(userIntoStudentDTO.nim)){
            throw new IllegalArgumentException("NIM/NIP must only contain number and maximal 12 digits long");
        }

        if(!GeneralUtils.isValidString(userIntoStudentDTO.fullName)){
            throw new IllegalArgumentException("Full name must only contain letters");
        }

        user.setNip(null);
        user.setFullName(userIntoStudentDTO.fullName);
        user.setNim(userIntoStudentDTO.nim);
        user.setRole(UserRole.STUDENT.getValue());

        try{
            userRepository.save(user);
            return new ResponseEntity<>(
                    new ResponseUpdateDTO("accept", "User updated to STUDENT"),
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

