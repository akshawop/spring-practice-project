package me.akshawop.journalApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.akshawop.journalApp.entity.JournalEntry;
import me.akshawop.journalApp.service.JournalEntryService;

@RestController
@RequestMapping("/admin/journal")
public class AdminJournalEntryController {

    @Autowired
    private JournalEntryService userService;

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllJournalEntries() {

        List<JournalEntry> entries = userService.getAllEntries();
        if (entries.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(entries, HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable String id) {

        JournalEntry entry = userService.getEntryByIdAdmin(id);
        return new ResponseEntity<>(entry, HttpStatus.OK);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<HttpStatus> deleteJournalById(@PathVariable String id) {

        userService.deleteEntryByIdAdmin(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
