package com.example.pfe_backend.service;

import com.example.pfe_backend.DTO.UpdateUserRequest;
import com.example.pfe_backend.model.Notification;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.repository.MessageRepository;
import com.example.pfe_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MessageRepository messageRepository;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.messageRepository = messageRepository;
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

//    System.out.println("Avatar file received: " + (avatarFile != null));
//
//    if (user.isEnabled()) {
//        user.setEnabled(true); // Assurez-vous que l'utilisateur reste activé
//    }
//
//    // Mise à jour de l'avatar
//    if (avatarFile != null && !avatarFile.isEmpty()) {
//        String contentType = avatarFile.getContentType();
//        if (contentType == null || !contentType.startsWith("image/")) {
//            throw new IllegalArgumentException("Invalid file type: must be an image");
//        }
//        try {
//            user.setAvatarType(contentType);
//            user.setAvatarData(avatarFile.getBytes());
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to process avatar file", e);
//        }
//    }
//
////    if(avatarFile != null && !avatarFile.isEmpty()) {
////        System.out.println("Processing avatar file...");
////        user.setAvatarType(avatarFile.getContentType());
////        user.setAvatarData(avatarFile.getBytes());
////    }

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

        // 1. Supprimer les messages liés
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
