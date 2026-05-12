package me.akshawop.journalApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.exception.UserNotFoundException;
import me.akshawop.journalApp.service.UserService;

@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {

        List<User> users = userService.getAllUsers();
        if (users.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<User> getUserByUsername(
            @NotEmpty(message = "username value is required in query parameter") @Size(min = 4, max = 20, message = "username must be between 4 to 20 characters") @RequestParam String username) {

        User user = userService.getUserByUsername(username);
        if (user == null)
            throw new UserNotFoundException(username, UserNotFoundException.USERNAME);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteUser(
            @NotEmpty(message = "username value is required in query parameter") @Size(min = 4, max = 20, message = "username must be between 4 to 20 characters") @RequestParam String username) {

        userService.deleteUserByUsername(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{username}/make-admin")
    public ResponseEntity<HttpStatus> makeAdmin(
            @NotEmpty(message = "username value is required in query parameter") @Size(min = 4, max = 20, message = "Username must be between 4 to 20 characters") @PathVariable String username) {

        userService.makeAdmin(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
