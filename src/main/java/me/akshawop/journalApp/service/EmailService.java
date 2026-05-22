package me.akshawop.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.github.sonus21.rqueue.annotation.RqueueListener;

import lombok.NonNull;
import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.util.queue.QueueConstants;
import me.akshawop.journalApp.util.queue.dto.EmailJob;

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

    @RqueueListener(value = QueueConstants.EMAIL_QUEUE, numRetries = "3")
    public void emailQueueConsumer(EmailJob job) {
        sendEmail(job.getTo(), job.getSubject(), job.getBody());
    }

    public void sendOTPVerificationMail(@NonNull String to, @NonNull String otp) {
        String body = String
                .format("Your OTP to verify your Email account is %s. Please do not share the OTP to anyone.", otp);
        String subject = "Verification Code to register to Journal App";
        sendEmail(to, subject, body);
    }

    public static EmailJob getSignupSuccessMail(@NonNull User user) {

        String subject = "Journal App account signup Successful";
        String body = String.format("Thank you for signing up in our application :)\nYour username is %s\n\nEnjoy!",
                user.getUsername());
        return new EmailJob("v1", user.getEmail(), subject, body);

    }
}
