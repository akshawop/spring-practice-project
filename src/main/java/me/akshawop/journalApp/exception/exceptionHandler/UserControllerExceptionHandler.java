package me.akshawop.journalApp.exception.exceptionHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import me.akshawop.journalApp.controller.UserController;
import me.akshawop.journalApp.exception.UsernameAlreadyTakenException;

@Order(1)
@RestControllerAdvice(assignableTypes = UserController.class)
public class UserControllerExceptionHandler {

    @Autowired
    private ResponseBuilder builder;

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyTaken(Exception ex, HttpServletRequest req) {

        return builder.buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                req,
                null);
    }
}
