//package id.ac.ui.cs.advprog.account.controller;
//import id.ac.ui.cs.advprog.account.service.AccountService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RestController;
//import java.util.Map;
//import id.ac.ui.cs.advprog.authjwt.model.User;
//
//@RestController
//@RequestMapping("/api/account")
//public class AccountController {
//    private final AccountService accountService;
//
//    public AccountController(AccountService accountService) {
//        this.accountService = accountService;
//    }
//
//    @PostMapping("/admin/delete")
//    public ResponseEntity<Map<String, String>> delete(@RequestBody User user) {
//        return accountService.delete(user.getUsername());
//    }
//}