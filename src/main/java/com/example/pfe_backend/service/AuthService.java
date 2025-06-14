package com.example.pfe_backend.service;

import com.example.pfe_backend.DTO.*;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;



    public User register(RegistrationRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Validation du rôle
        if (request.getRole() == null) {
            throw new RuntimeException("The role is mandatory");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(false);
        return userRepository.save(user);
    }

    public User registerByAdmin(RegistrationRequest request) {
        if (request.getRole() == null) {
            throw new RuntimeException("The role is mandatory");
        }

        // Vérifier l'unicité de l'email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(true); // Activer directement le compte

        User savedUser = userRepository.save(user);

        // Envoyer les credentials par email
        sendUserCredentials(savedUser, request.getPassword());

        return savedUser;
    }

    private void sendUserCredentials(User user, String plainPassword) {
        String subject = "Your Account Credentials";
        String content = "Your account has been created by administrator.\n\n"
                + "Login details:\n"
                + "Email: " + user.getEmail() + "\n"
                + "Password: " + plainPassword + "\n\n"
                + "Please change your password after first login.";

        emailService.sendEmail(user.getEmail(), subject, content);
    }


    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .user(user)
                .build();
    }


    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (user != null) {
            String token = UUID.randomUUID().toString();
            user.setResetPasswordToken(token);
            userRepository.save(user);
            String resetLink = "http://localhost:4200/auth/resetPassword/" + token;
            emailService.sendEmail(
                    user.getEmail(),
                    "Réinitialisation de votre mot de passe",
                    "Pour réinitialiser votre mot de passe, cliquez sur ce lien: " + resetLink
            );
        }
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }


}