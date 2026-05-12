package me.akshawop.journalApp.exception;

public class DuplicateUserRegistrationException extends RuntimeException {
    public DuplicateUserRegistrationException(String message) {
        super(message);
    }
}
