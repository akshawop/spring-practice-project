package me.akshawop.journalApp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import me.akshawop.journalApp.entity.JournalEntry;
import me.akshawop.journalApp.repository.JournalEntryRepo;

@Service
public class JournalEntryService {
    @Autowired
    private JournalEntryRepo repo;

    public void saveEntry(@NonNull JournalEntry entry) {
        repo.save(entry);
    }

    public Optional<JournalEntry> getEntryById(@NonNull String id) {
        return repo.findById(id);
    }

    public List<JournalEntry> getAll() {
        return repo.findAll();
    }

    public void deleteById(@NonNull String id) {
        repo.deleteById(id);
    }
}
