package me.akshawop.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);

            javaMailSender.send(mail);
        } catch (Exception e) {
            log.error("Exception occurred while sending mail: ", e);
        }
    }

    public void sendOTP(String to, String otp) {
        String body = String
                .format("Your OTP to verify your Email account is %s. Please do not share the OTP to anyone.", otp);
        String subject = "Verification Code to register to Journal App";
        sendEmail(to, subject, body);
    }

    public void sendSignupSuccessMail(String to, String username) {
        String subject = "Journal App account signup Successful";
        String body = String.format("Thank you for signing up in our application :)\nYour username is %s\n\nEnjoy!",
                username);
        sendEmail(to, subject, body);
    }
}
