package me.akshawop.journalApp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import me.akshawop.journalApp.entity.JournalEntry;

@Repository
public interface JournalEntryRepo extends MongoRepository<JournalEntry, String> {

}
