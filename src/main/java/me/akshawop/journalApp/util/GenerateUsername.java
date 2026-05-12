package me.akshawop.journalApp.util;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.akshawop.journalApp.exception.UsernameGenerationFailedException;
import me.akshawop.journalApp.repository.UserRepo;

@Component
public class GenerateUsername {
    @Autowired
    private UserRepo repo;

    public String generate(String email) {
        int maxRetries = 5;
        int iteration = 0;
        boolean success = false;

        // get the username from email and if length exceeds 10 characters, trim it
        String username = (email.substring(0, email.indexOf('@')));
        if (username.length() > 10) {
            username = username.substring(0, 10);
        }

        String newUsername = username + UUID.nameUUIDFromBytes(email.getBytes()).toString().substring(0, 5);

        do {

            if (repo.findByUsername(newUsername).isEmpty()) {
                success = true;
                break;
            }

            iteration++;

            if (iteration < maxRetries) {
                newUsername = username
                        + UUID.nameUUIDFromBytes(newUsername.getBytes()).toString().substring(0, 5 + iteration);
            }

        } while (iteration < maxRetries);

        if (!success)
            throw new UsernameGenerationFailedException("Failed to generate username for email " + email);
        return newUsername;
    }
}
