package me.akshawop.journalApp.exception.exceptionHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import me.akshawop.journalApp.controller.SignupController;
import me.akshawop.journalApp.exception.DuplicateUserRegistrationException;
import me.akshawop.journalApp.exception.OTPValidationFailedException;
import me.akshawop.journalApp.exception.UsernameGenerationFailedException;

@Order(1)
@RestControllerAdvice(assignableTypes = SignupController.class)
public class SignupControllerExceptionHandler {

    @Autowired
    private ResponseBuilder builder;

    @ExceptionHandler(DuplicateUserRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleConflict(Exception ex, HttpServletRequest req) {

        return builder.buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                req,
                null);
    }

    @ExceptionHandler(OTPValidationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuth(Exception ex, HttpServletRequest req) {

        return builder.buildResponse(
                HttpStatus.UNAUTHORIZED,
                "OTP validation failed",
                ex.getMessage(),
                req,
                null);
    }

    @ExceptionHandler(UsernameGenerationFailedException.class)
    public ResponseEntity<ErrorResponse> handleUsernameGenerationFailed(Exception ex, HttpServletRequest req) {

        return builder.buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                req,
                null);
    }
}
