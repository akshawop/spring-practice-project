package me.akshawop.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(@NonNull String to, @NonNull String subject, @NonNull String body) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);

            javaMailSender.send(mail);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in Email service", e);
        }
    }

    public void sendOTPVerificationMail(@NonNull String to, @NonNull String otp) {
        String body = String
                .format("Your OTP to verify your Email account is %s. Please do not share the OTP to anyone.", otp);
        String subject = "Verification Code to register to Journal App";
        sendEmail(to, subject, body);
    }

    public void sendSignupSuccessMail(@NonNull String to, @NonNull String username) {
        String subject = "Journal App account signup Successful";
        String body = String.format("Thank you for signing up in our application :)\nYour username is %s\n\nEnjoy!",
                username);
        sendEmail(to, subject, body);
    }
}
