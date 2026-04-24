package me.akshawop.journalApp.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import me.akshawop.journalApp.dto.UserSignupDTO;
import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.exceptions.UsernameGenerationFailedException;
import me.akshawop.journalApp.service.UserService;
import me.akshawop.journalApp.util.GenerateUsername;
import me.akshawop.journalApp.util.RegexPatterns;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    private UserService userService;

    @Autowired
    private GenerateUsername genUsername;

    @GetMapping("/health-check")
    public String healthCheck() {
        log.info("health-check route invoked.");
        return "ok!";
    }

    @PostMapping("/user/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupDTO userData) {
        try {
            // email and password validation
            boolean isEmailValid = RegexPatterns.EMAIL.matcher(userData.email()).find();
            boolean isPasswordValid = RegexPatterns.PASSWORD.matcher(userData.password()).find();
            if (!(isEmailValid && isPasswordValid)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // extract the username from email
            String username = userData.email().substring(0, userData.email().indexOf('@'));
            // check if the username already exists in db; if so, make it unique
            if (userService.getUserByUsername(username) != null) {
                username = genUsername.generate(userData.email());
            }

            User user = User.builder()
                    .username(username)
                    .email(userData.email())
                    .password(userData.password())
                    .joiningDate(LocalDateTime.now())
                    .build();

            userService.saveNewUser(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UsernameGenerationFailedException e) {
            return new ResponseEntity<>("could not generate a username for this user",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            user.setJoiningDate(LocalDateTime.now());
            userService.saveNewUser(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
