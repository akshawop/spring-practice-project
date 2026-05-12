package me.akshawop.journalApp.exception.exceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ResponseBuilder {

    public ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String title,
            String detail,
            HttpServletRequest req,
            List<String> errors) {

        ErrorResponse error = ErrorResponse.builder()
                .title(title)
                .detail(detail)
                .status(status.value())
                .instance(URI.create(req.getRequestURI()))
                .build();

        // Extensions (RFC 9457 style)
        error.addExtension("timestamp", Instant.now());

        if (errors != null && !errors.isEmpty()) {
            error.addExtension("errors", errors);
        }

        return new ResponseEntity<>(error, status);
    }

    public ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String detail,
            HttpServletRequest req,
            List<String> errors) {

        return buildResponse(status, status.getReasonPhrase(), detail, req, errors);
    }
}
