package me.akshawop.journalApp.exception.exceptionHandler;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private URI type; // e.g. https://example.com/errors/user-not-found

    @NonNull
    private String title; // short, human-readable summary

    private Integer status; // HTTP status code

    @NonNull
    private String detail; // detailed explanation

    private URI instance; // request URI or error occurrence ID

    // Extension fields (RFC 9457 allows arbitrary members)
    @Builder.Default
    private Map<String, Object> extensions = new HashMap<>();

    // extensions

    @JsonAnyGetter
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @JsonAnySetter
    public void addExtension(String key, Object value) {
        this.extensions.put(key, value);
    }
}