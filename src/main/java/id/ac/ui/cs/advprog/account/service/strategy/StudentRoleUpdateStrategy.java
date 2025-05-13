package id.ac.ui.cs.advprog.account.service.strategy;

import id.ac.ui.cs.advprog.account.dto.update.ResponseUpdateDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserIntoLecturerDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserIntoStudentDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserUpdateDTO;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("LECTURER")
public class StudentRoleUpdateStrategy implements RoleUpdateStrategy {

    private final UserRepository userRepository;

    public StudentRoleUpdateStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseUpdateDTO> updateRole(UserUpdateDTO updateData, User user) {
        UserIntoStudentDTO userIntoLecturerDTO = (UserIntoStudentDTO) updateData;

        user.setNip(null);
        user.setFullName(userIntoLecturerDTO.fullName);
        user.setNim(userIntoLecturerDTO.nim);
        user.setRole("STUDENT");

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

