package me.akshawop.journalApp.exception.exceptionHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import me.akshawop.journalApp.controller.JournalEntryController;
import me.akshawop.journalApp.exception.AccessDeniedException;

@Order(1)
@RestControllerAdvice(assignableTypes = JournalEntryController.class)
public class JournalEntryControllerExceptionHandler {

    @Autowired
    private ResponseBuilder builder;

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex, HttpServletRequest req) {

        return builder.buildResponse(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                ex.getMessage(),
                req,
                null);
    }
}
