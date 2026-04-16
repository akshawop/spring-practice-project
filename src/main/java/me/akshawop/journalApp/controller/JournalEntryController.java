package me.akshawop.journalApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.akshawop.journalApp.entity.JournalEntry;
import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.service.JournalEntryService;
import me.akshawop.journalApp.service.UserService;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {
    @Autowired
    private JournalEntryService journalService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesOfUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userService.getUserByUsername(username);
            if (user == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            List<JournalEntry> entries = user.getJournalEntries();
            if (entries.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(entries, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry entry) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userService.getUserByUsername(username);

            if (user == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            journalService.saveNewEntry(entry, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateJournalById(@PathVariable String id, @RequestBody JournalEntry entry) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userService.getUserByUsername(username);

            // checks if the said user exists
            if (user == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            JournalEntry oldEntry = journalService.getEntryById(id);
            // checks if the entry belongs to the user
            if (!user.getJournalEntries().contains(oldEntry))
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            // checks if both the updatable parameters are empty
            if (entry.getContent() == null && entry.getTitle() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            journalService.updateEntry(oldEntry, entry);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteJournalById(@PathVariable String id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userService.getUserByUsername(username);

            if (user == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            JournalEntry entry = journalService.getEntryById(id);
            if (entry == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            if (!user.getJournalEntries().contains(entry))
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            journalService.deleteEntry(entry, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
