package me.akshawop.journalApp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.NonNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.entity.UserRoles;
import me.akshawop.journalApp.exception.DuplicateUserRegistrationException;
import me.akshawop.journalApp.exception.UserNotFoundException;
import me.akshawop.journalApp.exception.UsernameAlreadyTakenException;
import me.akshawop.journalApp.model.UserDTO;
import me.akshawop.journalApp.repository.UserRepo;
import me.akshawop.journalApp.util.GenerateUsername;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GenerateUsername genUsername;

    @Autowired
    private KafkaTemplate<String, String> kafka;

    @SuppressWarnings("null")
    public User saveNewUser(@NonNull UserDTO userData) {

        if (userRepo.findByEmail(userData.getEmail()).isPresent())
            throw new DuplicateUserRegistrationException("This email is already registered");

        // extract the username from email
        String username = (userData.getEmail().substring(0,
                userData.getEmail().indexOf('@')));

        // pad with random uuid if length is less than 4
        if (username.length() < 4) {
            username = (username + UUID.randomUUID()).substring(0, 4);
        }

        if (username.length() > 20)
            username = username.substring(0, 20);

        // check if the username already exists in db; if so, try to generate new
        if (userRepo.findByUsername(username).isPresent()) {
            username = genUsername.generate(userData.getEmail());
        }

        User user = User.builder()
                .email(userData.getEmail())
                .password(userData.getPassword())
                .username(username)
                .roles(new ArrayList<>(List.of(UserRoles.USER)))
                .build();

        return userRepo.save(user);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getUserByUsername(@NonNull String username) {
        return userRepo.findByUsername(username)
                .orElse(null);
    }

    public User getUserByEmail(@NonNull String email) {
        return userRepo.findByEmail(email)
                .orElse(null);
    }

    // public void updateUser(@NonNull User oldUser, @NonNull User newUserData) {
    // // update and store the new data
    // oldUser.setUsername(newUserData.getUsername());
    // oldUser.setPassword(passwordEncoder.encode(newUserData.getPassword()));

    // userRepo.save(oldUser);
    // }

    // public void updateUserAdmin(@NonNull User oldUser, @NonNull User newUserData)
    // {
    // // update and store the new data
    // oldUser.setUsername(newUserData.getUsername());
    // oldUser.setPassword(passwordEncoder.encode(newUserData.getPassword()));
    // if (!newUserData.getRoles().isEmpty())
    // oldUser.setRoles(newUserData.getRoles());
    // if (newUserData.getJoiningDate() != null)
    // oldUser.setJoiningDate(newUserData.getJoiningDate());

    // userRepo.save(oldUser);
    // }

    @Transactional
    public void deleteUserByUsername(@NonNull String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username, UserNotFoundException.USERNAME));

        userRepo.deleteByUsername(user.getUsername());
        kafka.send("user.account.deleted", user.getId().toString());
    }

    public void makeAdmin(@NonNull String username) {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username, UserNotFoundException.USERNAME));

        user.getRoles().add(UserRoles.ADMIN);
        userRepo.save(user);
    }

    public User changeUsername(String oldUsername, String newUsername) {

        User user = userRepo.findByUsername(oldUsername)
                .orElseThrow(() -> new UserNotFoundException(oldUsername, UserNotFoundException.USERNAME));

        if (userRepo.findByUsername(newUsername).isPresent()) {
            throw new UsernameAlreadyTakenException("Username " + newUsername
                    + " is already taken, try a different username. You can check if a username is available on /check-username?username=value");
        }

        user.setUsername(newUsername);
        return userRepo.save(user);
    }
}
