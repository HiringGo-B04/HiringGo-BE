package id.ac.ui.cs.advprog.account.service.strategy;

import id.ac.ui.cs.advprog.account.dto.update.ResponseUpdateDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserUpdateDTO;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("STUDENT")
public class AdminRoleUpdateStrategy implements RoleUpdateStrategy {

    private final UserRepository userRepository;

    public AdminRoleUpdateStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseUpdateDTO> updateRole(UserUpdateDTO updateData, User user) {
        user.setNip(null);
        user.setFullName(null);
        user.setNim(null);
        user.setRole("ADMIN");

        try{
            userRepository.save(user);
            return new ResponseEntity<>(
                    new ResponseUpdateDTO("accept", "User updated to ADMIN"),
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

