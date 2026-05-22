package me.akshawop.journalApp.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.NonNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.entity.UserRoles;
import me.akshawop.journalApp.exception.DuplicateUserRegistrationException;
import me.akshawop.journalApp.exception.UserNotFoundException;
import me.akshawop.journalApp.exception.UsernameAlreadyTakenException;
import me.akshawop.journalApp.model.UserDTO;
import me.akshawop.journalApp.repository.UserRepo;
import me.akshawop.journalApp.util.GenerateUsername;
import me.akshawop.journalApp.util.queue.dto.EmailJob;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GenerateUsername genUsername;

    @Autowired
    private QueueService queueService;

    @SuppressWarnings("null")
    @Transactional
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
                .roles(Collections.singletonList(UserRoles.USER))
                .build();

        user = userRepo.save(user);
        EmailJob job = EmailService.getSignupSuccessMail(user);

        TransactionSynchronizationManager
                .registerSynchronization(
                        new TransactionSynchronization() {

                            @Override
                            public void afterCommit() {
                                queueService.publishEmail(job);
                            }
                        });

        return user;

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

    @Transactional
    public void deleteUserByUsername(@NonNull String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username, UserNotFoundException.USERNAME));

        userRepo.deleteByUsername(user.getUsername());

        TransactionSynchronizationManager
                .registerSynchronization(
                        new TransactionSynchronization() {

                            @Override
                            public void afterCommit() {
                                queueService.publishDeleteUserData(user.getId());
                            }
                        });
    }

    public void makeAdmin(@NonNull String username) {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username, UserNotFoundException.USERNAME));

        user.getRoles().add(UserRoles.ADMIN);
        userRepo.save(user);
    }

    public User changeUsername(User user, String newUsername) {

        if (userRepo.findByUsername(newUsername).isPresent()) {
            throw new UsernameAlreadyTakenException("Username " + newUsername
                    + " is already taken, try a different username. You can check if a username is available on /check-username?username=value");
        }

        user.setUsername(newUsername);
        return userRepo.save(user);
    }
}
