package me.akshawop.journalApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.NonNull;
import org.springframework.stereotype.Service;

import me.akshawop.journalApp.entity.JournalEntry;
import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.repository.UserRepo;

@Service
public class UserService {
    @Autowired
    private UserRepo repo;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void saveUser(@NonNull User user) {
        repo.save(user);
    }

    public void saveNewUser(@NonNull User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repo.save(user);
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User getUserByUsername(@NonNull String username) {
        return repo.findByUsername(username);
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
