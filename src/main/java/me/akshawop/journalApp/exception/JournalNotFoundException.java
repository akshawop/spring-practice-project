package me.akshawop.journalApp.exception;

public class JournalNotFoundException extends GenericNotFoundException {

    public JournalNotFoundException(String journalId) {
        super("Journal with id " + journalId + " not found");
    }

}
