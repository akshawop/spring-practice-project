package me.akshawop.journalApp.util.otp;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class OTPGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public String generateOtp() {
        int otp = secureRandom.nextInt(1_000_000);
        return String.format("%06d", otp);
    }
}