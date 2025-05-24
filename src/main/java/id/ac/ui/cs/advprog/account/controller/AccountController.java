package id.ac.ui.cs.advprog.account.controller;
import id.ac.ui.cs.advprog.account.dto.delete.DeleteRequestDTO;
import id.ac.ui.cs.advprog.account.dto.delete.DeleteResponseDTO;
import id.ac.ui.cs.advprog.account.dto.get.GetAllUserDTO;
import id.ac.ui.cs.advprog.account.dto.update.ResponseUpdateDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserUpdateDTO;
import id.ac.ui.cs.advprog.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    public static final String USER = "/admin/user";

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @DeleteMapping(USER)
    public ResponseEntity<DeleteResponseDTO> delete(@Valid @RequestBody DeleteRequestDTO email) {
        return accountService.delete(email);
    }

    @PatchMapping(USER)
    public ResponseEntity<ResponseUpdateDTO> update(@Valid @RequestBody UserUpdateDTO data) {
        return accountService.update(data);
    }


    @GetMapping(USER)
    public ResponseEntity<GetAllUserDTO> get() throws ExecutionException, InterruptedException {
        return accountService.getAllUser().get(); // waits on async task
    }

}