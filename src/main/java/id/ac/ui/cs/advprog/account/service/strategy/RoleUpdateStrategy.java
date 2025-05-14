package id.ac.ui.cs.advprog.account.service.strategy;

import id.ac.ui.cs.advprog.account.dto.update.ResponseUpdateDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserUpdateDTO;
import id.ac.ui.cs.advprog.authjwt.model.User;
import org.springframework.http.ResponseEntity;

public interface RoleUpdateStrategy {
    ResponseEntity<ResponseUpdateDTO> updateRole(UserUpdateDTO updateData, User user);
}
