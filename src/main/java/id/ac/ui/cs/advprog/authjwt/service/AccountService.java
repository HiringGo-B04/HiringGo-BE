package id.ac.ui.cs.advprog.authjwt.service;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AccountService{
    @Autowired
    UserRepository userRepository;

    @Transactional
    public ResponseEntity<Map<String, String>> delete(String email){
        Map<String, String> response = new HashMap<>();
        try {
            if(email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email is empty");
            }

            User user = userRepository.findByUsername(email);
            if(user == null) {
                throw new IllegalArgumentException("User not found");
            }

            userRepository.deleteByUsername(user.getUsername());

            response.put("status", "error");
            response.put("messages", "Succes delete user");
            return new ResponseEntity<>(response, HttpStatus.valueOf(200));
        }
        catch (Exception e) {
            response.put("status", "error");
            response.put("messages", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.valueOf(403));
        }
    }
}