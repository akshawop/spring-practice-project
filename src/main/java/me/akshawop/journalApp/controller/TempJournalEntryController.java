package me.akshawop.journalApp.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.akshawop.journalApp.entity.JournalEntry;

@RestController
@RequestMapping("/temp-journal")
public class TempJournalEntryController {
    private Map<String, JournalEntry> journalEntries = new HashMap<>();

    @GetMapping
    public List<JournalEntry> getAll() {
        return new ArrayList<>(journalEntries.values());
    }

    @GetMapping("id/{id}")
    public JournalEntry getJournalEntryById(@PathVariable String id) {
        return journalEntries.get(id);
    }

    @PostMapping
    public String createEntry(@RequestBody JournalEntry entry) {
        entry.setDateTime(LocalDateTime.now());
        journalEntries.put(entry.getId(), entry);
        return "Done";
    }

    @DeleteMapping("id/{id}")
    public JournalEntry deleteJournalById(@PathVariable String id) {
        return journalEntries.remove(id);
    }

    @PutMapping("id/{id}")
    public JournalEntry updateJournalById(@PathVariable String id, @RequestBody JournalEntry entry) {
        return journalEntries.put(id, entry);
    }
}
