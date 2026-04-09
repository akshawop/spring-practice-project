package me.akshawop.journalApp.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.akshawop.journalApp.entity.JournalEntry;
import me.akshawop.journalApp.service.JournalEntryService;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {
    @Autowired
    private JournalEntryService service;

    @GetMapping
    public List<JournalEntry> getAll() {
        return service.getAll();
    }

    @GetMapping("id/{id}")
    public JournalEntry getJournalEntryById(@NonNull @PathVariable String id) {
        return service.getEntryById(id).orElse(null);
    }

    @PostMapping
    public String createEntry(@RequestBody JournalEntry entry) {
        entry.setDateTime(LocalDateTime.now());
        service.saveEntry(entry);
        return "Done";
    }

    @DeleteMapping("id/{id}")
    public String deleteJournalById(@NonNull @PathVariable String id) {
        service.deleteById(id);
        return "Done";
    }

    @PutMapping("id/{id}")
    public String updateJournalById(@NonNull @PathVariable String id, @RequestBody JournalEntry entry) {
        JournalEntry oldEntry = getJournalEntryById(id);

        // checks if the said entry exists
        if (oldEntry == null)
            return null;

        // checks if both the updatable parameters aren't empty
        if (entry.getContent() == null && entry.getTitle() == null)
            return null;

        // update and store the new data
        if (entry.getTitle() != null)
            oldEntry.setTitle(entry.getTitle());
        if (entry.getContent() != null)
            oldEntry.setContent(entry.getContent());
        oldEntry.setDateTime(LocalDateTime.now());

        service.saveEntry(oldEntry);
        return "Done";
    }
}
