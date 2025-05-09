package id.ac.ui.cs.advprog.account.service;

import id.ac.ui.cs.advprog.account.dto.delete.DeleteRequestDTO;
import id.ac.ui.cs.advprog.account.dto.delete.DeleteResponseDTO;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

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
}