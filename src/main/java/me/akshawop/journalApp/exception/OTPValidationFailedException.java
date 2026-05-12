package me.akshawop.journalApp.exception;

public class OTPValidationFailedException extends RuntimeException {
    public OTPValidationFailedException(String message) {
        super(message);
    }
}
