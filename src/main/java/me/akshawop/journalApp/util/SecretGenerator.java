package me.akshawop.journalApp.util;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateBase64Secret(int numBytes) {
        byte[] key = new byte[numBytes]; // e.g., 32 bytes = 256 bits
        secureRandom.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}