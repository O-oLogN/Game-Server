package com.us.gamewithserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EmailService {


    @Autowired
    private JavaMailSenderImpl mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String fromEmail = "tranhung10122003@gmail.com";
        String subject = "Password Reset Request";
        String message = "You requested a password reset. Use the following token to reset your password:\n\n"
                + resetToken + "\n\n"
                + "This token is valid for 15 minutes.";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setFrom(fromEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setSentDate(new Date());

        mailSender.send(mailMessage);
    }
}
