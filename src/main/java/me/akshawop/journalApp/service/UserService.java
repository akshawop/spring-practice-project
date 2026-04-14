package me.akshawop.journalApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import me.akshawop.journalApp.entity.JournalEntry;
import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.repository.UserRepo;

@Service
public class UserService {
    @Autowired
    private UserRepo repo;

    public void saveUser(@NonNull User user) {
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

    public void deleteUserByUsername(@NonNull String username) {
        repo.deleteByUsername(username);
    }
}
