package com.example.pfe_backend.service;

import com.example.pfe_backend.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }


    @Async
    public void sendEmailNotification(User receiver, User sender, String messageContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Configuration du contexte Thymeleaf
            Context context = new Context();
            context.setVariable("receiver", receiver);
            context.setVariable("sender", sender);
            context.setVariable("message", messageContent);
            context.setVariable("applicationUrl", "http://votre-app.com");

            String htmlContent = templateEngine.process("email-notification", context);

            helper.setTo(receiver.getEmail());
            helper.setSubject("Nouveau message de " + sender.getUsername());
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            log.error("error send email to {} : {}", receiver.getEmail(), e.getMessage());
        } catch (Exception e) {
            log.error("Error sending email : {}", e.getMessage());
        }
    }

}
