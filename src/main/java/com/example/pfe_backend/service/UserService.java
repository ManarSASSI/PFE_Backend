package com.example.pfe_backend.service;

import com.example.pfe_backend.model.Notification;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }


    // Méthodes CRUD
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setPassword(userDetails.getPassword());
        user.setAvatar(userDetails.getAvatar());
        user.setPhone(userDetails.getPhone());
        user.setLocation(userDetails.getLocation());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.delete(getUserById(id));
    }

    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> getPendingUsers() {
        return userRepository.findByEnabled(false);
    }

    public User approveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

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
