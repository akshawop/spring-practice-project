package me.akshawop.journalApp.exception.exceptionHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@Order(100)
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @Autowired
        private ResponseBuilder builder;

        // 400 - VALIDATION ERRORS
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidation(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest req) {

                List<String> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                                .collect(Collectors.toList());

                return builder.buildResponse(
                                HttpStatus.BAD_REQUEST,
                                "Validation Failed",
                                "Request validation failed",
                                req,
                                errors);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleUnreadable(Exception ex, HttpServletRequest req) {

                return builder.buildResponse(
                                HttpStatus.BAD_REQUEST,
                                "Malformed JSON",
                                "Request body is invalid or unreadable",
                                req,
                                null);
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                        HttpServletRequest req) {

                List<String> errors = ex.getConstraintViolations()
                                .parallelStream()
                                .map(err -> err.getPropertyPath().toString().split("\\.", 2)[1] + ": "
                                                + err.getMessage())
                                .collect(Collectors.toList());

                return builder.buildResponse(
                                HttpStatus.BAD_REQUEST,
                                "Validation Failed",
                                "Request validation failed",
                                req,
                                errors);
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ErrorResponse> handleMissingRequestParams(Exception ex, HttpServletRequest req) {

                return builder.buildResponse(
                                HttpStatus.BAD_REQUEST,
                                "Missing query parameter",
                                ex.getMessage(),
                                req,
                                null);
        }

        // 401 - AUTHENTICATION
        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuth(Exception ex, HttpServletRequest req) {

                return builder.buildResponse(
                                HttpStatus.UNAUTHORIZED,
                                "Authentication failed",
                                req,
                                null);
        }

        // 403 - AUTHORIZATION
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex, HttpServletRequest req) {

                return builder.buildResponse(
                                HttpStatus.FORBIDDEN,
                                "You do not have permission to access this resource",
                                req,
                                null);
        }

        // 404 - NOT FOUND
        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleEntityNotFound(Exception ex, HttpServletRequest req) {

                return builder.buildResponse(
                                HttpStatus.NOT_FOUND,
                                "Entity Not Found",
                                req,
                                null);
        }

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFound(Exception ex, HttpServletRequest req) {

                return builder.buildResponse(
                                HttpStatus.NOT_FOUND,
                                "Resource Not Found",
                                req,
                                null);
        }

        // 409 - CONFLICT
        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> handleConflict(Exception ex, HttpServletRequest req) {

                return builder.buildResponse(
                                HttpStatus.CONFLICT,
                                "Data integrity violation",
                                req,
                                null);
        }

        // 503 - DATABASE / SERVICE DOWN
        @ExceptionHandler(SQLException.class)
        public ResponseEntity<ErrorResponse> handleDatabase(Exception ex, HttpServletRequest req) {

                log.error("Unhandled exception at [{} {}] -> {}",
                                req.getMethod(),
                                req.getRequestURI(),
                                ex.getMessage(),
                                ex);

                return builder.buildResponse(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "Database error occurred",
                                req,
                                null);
        }

        // 500 - GENERIC FALLBACK
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {

                log.error("Unhandled exception at [{} {}] -> {}",
                                req.getMethod(),
                                req.getRequestURI(),
                                ex.getMessage(),
                                ex);

                return builder.buildResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Something went wrong",
                                req,
                                null);
        }
}
