package me.akshawop.journalApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.akshawop.journalApp.service.UserService;

@RestController
public class UtilityController {
    @Autowired
    private UserService userService;

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        try {
            if (username.length() == 0)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            if (userService.getUserByUsername(username) != null)
                return new ResponseEntity<>(HttpStatus.FOUND);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}