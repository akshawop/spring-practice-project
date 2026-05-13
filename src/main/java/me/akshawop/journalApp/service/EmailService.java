package me.akshawop.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import me.akshawop.journalApp.entity.User;

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

    @KafkaListener(topics = "user.account.created", groupId = "signup-success-email-service-group")
    public void sendSignupSuccessMail(@NonNull User user, Acknowledgment ack) {
        String subject = "Journal App account signup Successful";
        String body = String.format("Thank you for signing up in our application :)\nYour username is %s\n\nEnjoy!",
                user.getUsername());
        sendEmail(user.getEmail(), subject, body);

        ack.acknowledge();
    }
}
