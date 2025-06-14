package com.example.pfe_backend.service;

import com.example.pfe_backend.DTO.UpdateUserRequest;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MessageRepository messageRepository;
    private final AlertRepository alertRepository;
    private final ContratRepository contratRepository;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, MessageRepository messageRepository, AlertRepository alertRepository, ContratRepository contratRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.messageRepository = messageRepository;
        this.alertRepository = alertRepository;
        this.contratRepository = contratRepository;
    }


    // Méthodes CRUD
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already used");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


//    public User updateAvatar(Long id, MultipartFile file) throws IOException {
//        if(file.isEmpty()) {
//            throw new IllegalArgumentException("File is empty");
//        }
//        String contentType = file.getContentType();
//        if(contentType == null || !contentType.startsWith("image/")) {
//            throw new IllegalArgumentException("Invalid file type");
//        }
//
//        User user = getUserById(id);
//        user.setAvatarType(file.getContentType());
//        user.setAvatarData(file.getBytes());
//        return userRepository.save(user);
//    }
@Transactional
public User updateUser(Long id, UpdateUserRequest updateRequest) throws IOException {
    User user = userRepository.findById(id).orElseThrow();

    // Mise à jour des autres champs
    user.setUsername(updateRequest.getUsername());
    user.setEmail(updateRequest.getEmail());
    user.setPhone(updateRequest.getPhone());
    user.setLocation(updateRequest.getLocation());

    if(updateRequest.getPassword() != null
            && !updateRequest.getPassword().trim().isEmpty()) {
        user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
    }

    return userRepository.save(user);
}

    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        alertRepository.deleteByRecipient(user);

        contratRepository.deleteByPartner(user);

        messageRepository.deleteBySender(user);
        messageRepository.deleteByReceiver(user);

        // 2. Mettre à jour les références 'created_by' dans d'autres utilisateurs
        List<User> dependentUsers = userRepository.findByCreatedBy(user);
        dependentUsers.forEach(u -> u.setCreatedBy(null));
        userRepository.saveAll(dependentUsers);

        userRepository.delete(user);
    }

    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> getPendingUsers() {
        return userRepository.findByEnabled(false);
    }

    public User approveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(true);
        User savedUser = userRepository.save(user);

        emailService.sendEmail(
                user.getEmail(),
                "Compte activé",
                "Votre compte a été approuvé par l'administrateur. Vous pouvez maintenant vous connecter."
        );

        return savedUser;
    }
}
