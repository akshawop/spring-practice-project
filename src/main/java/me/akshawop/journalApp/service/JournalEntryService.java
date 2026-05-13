package me.akshawop.journalApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import lombok.NonNull;
import org.springframework.stereotype.Service;

import me.akshawop.journalApp.entity.JournalEntry;
import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.exception.AccessDeniedException;
import me.akshawop.journalApp.exception.JournalNotFoundException;
import me.akshawop.journalApp.exception.UserNotFoundException;
import me.akshawop.journalApp.model.JournalEntryDTO;
import me.akshawop.journalApp.repository.JournalEntryRepo;
import me.akshawop.journalApp.repository.UserRepo;

@Service
public class JournalEntryService {
    @Autowired
    private JournalEntryRepo journalRepo;

    @Autowired
    private UserRepo userRepo;

    @SuppressWarnings("null")
    public JournalEntry saveEntry(@NonNull JournalEntryDTO newEntry, @NonNull String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username, UserNotFoundException.USERNAME));

        JournalEntry entry = JournalEntry.builder()
                .title(newEntry.getTitle())
                .content(newEntry.getContent())
                .userId(user.getId().toString())
                .build();

        return journalRepo.save(entry);
    }

    public JournalEntry getEntryById(String journalId, String username) {

        // check if the journal exists
        @SuppressWarnings("null")
        JournalEntry journalEntry = journalRepo.findById(journalId)
                .orElseThrow(() -> new JournalNotFoundException(journalId));

        // checks if the entry belongs to the user
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username, UserNotFoundException.USERNAME));
        if (!journalEntry.getUserId().equals(user.getId().toString()))
            throw new AccessDeniedException("You are NOT the owner of this journal");

        return journalEntry;
    }

    public JournalEntry getEntryByIdAdmin(@NonNull String id) {
        return (journalRepo.findById(id)).orElseThrow(() -> new JournalNotFoundException(id));
    }

    public List<JournalEntry> getAllEntriesForUser(@NonNull String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username, UserNotFoundException.USERNAME));
        return journalRepo.findAllByUserId(user.getId().toString());
    }

    public List<JournalEntry> getAllEntries() {
        return journalRepo.findAll();
    }

    public JournalEntry updateEntry(@NonNull String journalId, @NonNull JournalEntryDTO newEntryData,
            @NonNull String username) {

        // check if the journal exists
        JournalEntry oldEntry = journalRepo.findById(journalId)
                .orElseThrow(() -> new JournalNotFoundException(journalId));

        // checks if the entry belongs to the user
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username, UserNotFoundException.USERNAME));
        if (!oldEntry.getUserId().equals(user.getId().toString()))
            throw new AccessDeniedException("You are NOT authorized to update this journal");

        if (newEntryData.getTitle() != null)
            oldEntry.setTitle(newEntryData.getTitle());
        oldEntry.setContent(newEntryData.getContent());

        return journalRepo.save(oldEntry);
    }

    public void deleteEntryById(@NonNull String journalId, @NonNull String username) {
        // check if the journal exists
        JournalEntry entry = journalRepo.findById(journalId)
                .orElseThrow(() -> new JournalNotFoundException(journalId));

        // checks if the entry belongs to the user
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username, UserNotFoundException.USERNAME));
        if (!entry.getUserId().equals(user.getId().toString()))
            throw new AccessDeniedException("You are NOT authorized to delete this journal");

        journalRepo.deleteById(journalId);
    }

    public void deleteEntryByIdAdmin(@NonNull String journalId) {

        // check if the journal exists
        journalRepo.findById(journalId)
                .orElseThrow(() -> new JournalNotFoundException(journalId));

        journalRepo.deleteById(journalId);
    }

    @KafkaListener(topics = "user.account.deleted", groupId = "user-deletion-journal-cleanup-group")
    public void deleteEntryByUserId(String userId, Acknowledgment ack) {
        journalRepo.deleteAllByUserId(userId);

        ack.acknowledge();
    }
}
