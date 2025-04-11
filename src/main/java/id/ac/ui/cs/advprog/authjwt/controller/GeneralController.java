package id.ac.ui.cs.advprog.authjwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneralController {
    @GetMapping("/")
    public String helloWorld() {
        return "Hello World";
    }
}
