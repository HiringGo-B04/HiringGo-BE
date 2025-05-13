package id.ac.ui.cs.advprog.account.service;

import id.ac.ui.cs.advprog.account.dto.delete.DeleteRequestDTO;
import id.ac.ui.cs.advprog.account.dto.delete.DeleteResponseDTO;
import id.ac.ui.cs.advprog.account.dto.update.ResponseUpdateDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserUpdateDTO;
import id.ac.ui.cs.advprog.account.service.strategy.AdminRoleUpdateStrategy;
import id.ac.ui.cs.advprog.account.service.strategy.LecturerRoleUpdateStrategy;
import id.ac.ui.cs.advprog.account.service.strategy.RoleUpdateStrategy;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService{
    private final UserRepository userRepository;

    public AccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseEntity<DeleteResponseDTO> delete(DeleteRequestDTO email){
        try {
            User user = userRepository.findByUsername(email.email());
            if(user == null) {
                throw new IllegalArgumentException("User not found");
            }

            userRepository.deleteByUsername(user.getUsername());

            return new ResponseEntity<>(
                    new DeleteResponseDTO("accept", "Succes delete user"),
                    HttpStatus.valueOf(200));
        }

        catch (Exception e) {
            return new ResponseEntity<>(
                    new DeleteResponseDTO("error", e.getMessage()),
                    HttpStatus.valueOf(400));
        }
    }

    public ResponseEntity<ResponseUpdateDTO> update(UserUpdateDTO userUpdateDTO){
        try{
            User user = userRepository.findByUsername(userUpdateDTO.username);
            if(user == null) {
                throw new IllegalArgumentException("User not found");
            }

            RoleUpdateStrategy strategy;
            if(userUpdateDTO.role.equals("ADMIN")){
                strategy = new AdminRoleUpdateStrategy(userRepository);
            }
            else if(userUpdateDTO.role.equals("LECTURER")){
                strategy = new LecturerRoleUpdateStrategy(userRepository);
            }
            else{
                throw new IllegalArgumentException("Role not found");
            }

            return strategy.updateRole(userUpdateDTO, user);
        }
        catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseUpdateDTO("error", e.getMessage()),

                    HttpStatus.valueOf(400));
        }
    }
}