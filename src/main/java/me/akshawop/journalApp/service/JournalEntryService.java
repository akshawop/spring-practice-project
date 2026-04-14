package me.akshawop.journalApp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.akshawop.journalApp.entity.JournalEntry;
import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.repository.JournalEntryRepo;

@Service
public class JournalEntryService {
    @Autowired
    private JournalEntryRepo repo;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(@NonNull JournalEntry entry, @NonNull User user) {
        entry.setCreatedAt(LocalDateTime.now());
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

    @Transactional
    @SuppressWarnings("null")
    public void deleteEntry(@NonNull JournalEntry entry, @NonNull User user) {
        user.getJournalEntries().remove(entry);
        userService.saveUser(user);
        repo.deleteById(entry.getId());
    }
}
