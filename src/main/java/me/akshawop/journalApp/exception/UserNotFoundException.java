package me.akshawop.journalApp.exception;

public class UserNotFoundException extends GenericNotFoundException {

    public static final String USERNAME = "User with username %s not found";
    public static final String EMAIL = "User with email %s not found";

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String value, String template) {
        super(String.format(template, value));
    }
}
