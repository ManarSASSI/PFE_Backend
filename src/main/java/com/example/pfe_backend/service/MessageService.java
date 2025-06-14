package com.example.pfe_backend.service;

import com.example.pfe_backend.model.Alert;
import com.example.pfe_backend.model.Message;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.repository.MessageRepository;
import com.example.pfe_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.time.LocalDateTime;


@Service
@AllArgsConstructor
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private final MessageRepository messageRepository;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Async
    public void sendNotificationEmail(User receiver, User sender, String messageContent) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiver.getEmail());
        mailMessage.setSubject("Nouveau message de " + sender.getUsername());
        mailMessage.setText("Bonjour " + receiver.getUsername() + ",\n\n"
                + "Vous avez reçu un nouveau message de " + sender.getUsername() + "\n\n"

                +"Connectez-vous pour répondre : http://localhost:4200/auth/login" + "\n\n"
                + "Ceci est un message automatique, merci de ne pas y répondre.");
        mailSender.send(mailMessage);
    }


    public Message sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

        Message message = new Message();
        message.setContent(content);
        message.setSender(sender);
        message.setReceiver(receiver);
        Message savedMessage = messageRepository.save(message);

        sendNotificationEmail(receiver, sender, content);
        message.setStatus(Message.MessageStatus.DELIVERED);
        return savedMessage;
    }

    public List<Message> getConversation(Long userId1, Long userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return messageRepository.findBySenderAndReceiverOrderByTimestampDesc(user1, user2);
    }

    public List<Message> getUserMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return messageRepository.findByReceiverOrderByTimestampDesc(user);
    }

    public void deleteMessage(Long alertId) {
        Message message = messageRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alerte non trouvée avec l'ID : " + alertId));

        messageRepository.delete(message);
        log.info("Alerte supprimée avec l'ID : {}", alertId);
    }

}
