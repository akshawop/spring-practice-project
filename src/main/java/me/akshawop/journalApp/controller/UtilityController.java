package me.akshawop.journalApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import me.akshawop.journalApp.service.UserService;

@RestController
@Validated
public class UtilityController {
    @Autowired
    private UserService userService;

    @GetMapping("/check-username")
    public ResponseEntity<HttpStatus> checkUsername(
            @NotEmpty(message = "username value is required in query parameter") @Size(min = 4, max = 20, message = "username must be between 4 to 20 characters") @RequestParam String username) {

        if (userService.getUserByUsername(username) != null)
            return new ResponseEntity<>(HttpStatus.FOUND);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}