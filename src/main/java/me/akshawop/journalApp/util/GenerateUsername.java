package me.akshawop.journalApp.util;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.akshawop.journalApp.exceptions.UsernameGenerationFailedException;
import me.akshawop.journalApp.repository.UserRepo;

@Component
public class GenerateUsername {
    @Autowired
    private UserRepo repo;

    public String generate(String email) throws UsernameGenerationFailedException, Exception {
        int maxRetries = 5;
        int iteration = 0;
        boolean success = false;

        String username = email.substring(0, email.indexOf('@'));
        String newUsername = username + UUID.nameUUIDFromBytes(email.getBytes()).toString().substring(0, 6);
        do {
            if (repo.findByUsername(username) == null) {
                success = true;
                break;
            }
            newUsername = username + UUID.nameUUIDFromBytes(newUsername.getBytes()).toString().substring(0, 6);
            iteration++;
        } while (iteration < maxRetries);

        if (!success)
            throw new UsernameGenerationFailedException();
        return newUsername;
    }
}
