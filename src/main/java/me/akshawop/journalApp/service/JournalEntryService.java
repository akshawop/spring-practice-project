package me.akshawop.journalApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import me.akshawop.journalApp.entity.JournalEntry;
import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.repository.JournalEntryRepo;

@Service
public class JournalEntryService {
    @Autowired
    private JournalEntryRepo repo;

    @Autowired
    private UserService userService;

    public void saveEntry(@NonNull JournalEntry entry, User user) {
        entry = repo.save(entry);
        user.getJournalEntries().add(entry);
        userService.saveUser(user);
    }

    public void saveEntry(@NonNull JournalEntry entry) {
        repo.save(entry);
    }

    public JournalEntry getEntryById(@NonNull String id) {
        return (repo.findById(id)).orElse(null);
    }

    public List<JournalEntry> getAllEntries() {
        return repo.findAll();
    }

    public void deleteEntryById(@NonNull String id) {
        repo.deleteById(id);
    }

    @SuppressWarnings("null")
    public void deleteEntry(@NonNull JournalEntry entry, @NonNull User user) {
        user.getJournalEntries().remove(entry);
        userService.saveUser(user);
        repo.deleteById(entry.getId());
    }
}
