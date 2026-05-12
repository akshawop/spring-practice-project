package me.akshawop.journalApp.exception.exceptionHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import me.akshawop.journalApp.exception.GenericNotFoundException;

@Order(10)
@RestControllerAdvice("me.akshawop.journalApp.controller")
public class CommonCustomExceptionHandler {

        @Autowired
        private ResponseBuilder builder;

        @ExceptionHandler(GenericNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFound(Exception ex, HttpServletRequest req) {

                return builder.buildResponse(
                                HttpStatus.NOT_FOUND,
                                ex.getMessage(),
                                req,
                                null);
        }
}
