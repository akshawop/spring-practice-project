package me.akshawop.journalApp.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<JournalEntry>> getAll() {
        try {
            List<JournalEntry> entries = service.getAll();
            if (entries == null || entries.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return new ResponseEntity<>(entries, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("id/{id}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@NonNull @PathVariable String id) {
        try {
            Optional<JournalEntry> entry = service.getEntryById(id);
            if (entry.isPresent())
                return new ResponseEntity<>(entry.get(), HttpStatus.OK);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry entry) {
        try {
            entry.setDateTime(LocalDateTime.now());
            service.saveEntry(entry);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("id/{id}")
    public ResponseEntity<?> deleteJournalById(@NonNull @PathVariable String id) {
        try {
            service.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("id/{id}")
    public ResponseEntity<?> updateJournalById(@NonNull @PathVariable String id, @RequestBody JournalEntry entry) {
        try {
            Optional<JournalEntry> temp = service.getEntryById(id);
            JournalEntry oldEntry = temp.isPresent() ? temp.get() : null;

            // checks if the said entry exists
            if (oldEntry == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            // checks if both the updatable parameters aren't empty
            if (entry.getContent() == null && entry.getTitle() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            // update and store the new data
            if (entry.getTitle() != null)
                oldEntry.setTitle(entry.getTitle());
            if (entry.getContent() != null)
                oldEntry.setContent(entry.getContent());
            oldEntry.setDateTime(LocalDateTime.now());

            service.saveEntry(oldEntry);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
