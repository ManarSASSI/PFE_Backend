package com.example.pfe_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;


    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

//    public void sendAlertEmail(String to, String message) {
//        try {
//            SimpleMailMessage email = new SimpleMailMessage();
//            email.setTo(to);
//            email.setSubject("Alerte Contrat");
//            email.setText(message);
//            mailSender.send(email);
//            log.info("Email envoyé à {}", to);
//        } catch (MailException e) {
//            log.error("Erreur lors de l'envoi de l'email à {}: {}", to, e.getMessage());
//        }
//    }
//
//    public void sendPasswordResetEmail(String to, String resetLink) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("Password Reset Request");
//        message.setText("To reset your password, click the link below:\n" + resetLink);
//        mailSender.send(message);
//    }


}
