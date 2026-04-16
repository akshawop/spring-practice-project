package me.akshawop.journalApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.akshawop.journalApp.entity.JournalEntry;
import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.service.JournalEntryService;
import me.akshawop.journalApp.service.UserService;

@RestController
@RequestMapping("/admin/journal")
public class AdminJournalEntryController {
    @Autowired
    private JournalEntryService journalService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllJournalEntries() {
        try {
            List<JournalEntry> entries = journalService.getAllEntries();
            if (entries.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return new ResponseEntity<>(entries, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable String id) {
        try {
            JournalEntry entry = journalService.getEntryById(id);
            if (entry == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(entry, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateJournalById(@PathVariable String id, @RequestBody JournalEntry entry) {
        try {
            JournalEntry oldEntry = journalService.getEntryById(id);
            // checks if the said entry exists
            if (oldEntry == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            // checks if both the updatable parameters are empty
            if (entry.getContent() == null && entry.getTitle() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            // update and store the new data
            if (entry.getTitle() != null)
                oldEntry.setTitle(entry.getTitle());
            if (entry.getContent() != null)
                oldEntry.setContent(entry.getContent());
            if (entry.getCreatedAt() != null)
                oldEntry.setCreatedAt(entry.getCreatedAt());

            journalService.saveEntry(oldEntry);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{username}/{id}")
    public ResponseEntity<?> updateJournalOwner(@PathVariable String username,
            @PathVariable String id) {
        try {
            JournalEntry entry = journalService.getEntryById(id);
            // checks if the said entry exists
            if (entry == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            User user = userService.getUserByUsername(username);
            // checks if the said user exists
            if (user == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            userService.assignJournalToUser(user, entry);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteJournalById(@PathVariable String id) {
        try {
            JournalEntry entry = journalService.getEntryById(id);
            if (entry == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            journalService.deleteEntryById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
