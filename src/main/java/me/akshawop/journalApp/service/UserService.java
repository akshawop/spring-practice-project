package me.akshawop.journalApp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import me.akshawop.journalApp.entity.JournalEntry;
import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.exceptions.UsernameGenerationFailedException;
import me.akshawop.journalApp.repository.UserRepo;
import me.akshawop.journalApp.util.GenerateUsername;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepo repo;

    @Autowired
    private GenerateUsername genUsername;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(@NonNull User user) {
        repo.save(user);
    }

    public int saveNewUser(@NonNull User user) {
        try {

            // extract the username from email
            String username = user.getEmail().substring(0,
                    user.getEmail().indexOf('@'));
            // check if the username already exists in db; if so, make it unique
            if (getUserByUsername(username) != null) {
                username = genUsername.generate(user.getEmail());
            }
            user.setUsername(username);
            user.setJoiningDate(LocalDateTime.now());

            repo.save(user);
            return 0;
        } catch (UsernameGenerationFailedException e) {
            log.error("could not generate a username for this user", e);
            return 1;
        } catch (Exception e) {
            log.error("Error occurred while saving user: ", e);
            return 1;
        }
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User getUserByUsername(@NonNull String username) {
        return repo.findByUsername(username);
    }

    public User getUserByEmail(@NonNull String email) {
        return repo.findByEmail(email);
    }

    public void assignJournalToUser(@NonNull User user, @NonNull JournalEntry entry) {
        if (user.getJournalEntries().contains(entry))
            return;

        user.getJournalEntries().add(entry);
        repo.save(user);
    }

    public void updateUser(@NonNull User oldUser, @NonNull User newUserData) {
        // update and store the new data
        oldUser.setUsername(newUserData.getUsername());
        oldUser.setPassword(passwordEncoder.encode(newUserData.getPassword()));

        repo.save(oldUser);
    }

    public void updateUserAdmin(@NonNull User oldUser, @NonNull User newUserData) {
        // update and store the new data
        oldUser.setUsername(newUserData.getUsername());
        oldUser.setPassword(passwordEncoder.encode(newUserData.getPassword()));
        if (!newUserData.getRoles().isEmpty())
            oldUser.setRoles(newUserData.getRoles());
        if (newUserData.getJoiningDate() != null)
            oldUser.setJoiningDate(newUserData.getJoiningDate());

        repo.save(oldUser);
    }

    public void deleteUserByUsername(@NonNull String username) {
        repo.deleteByUsername(username);
    }
}
