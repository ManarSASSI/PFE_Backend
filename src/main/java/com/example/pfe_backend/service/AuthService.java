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
            throw new RuntimeException("Les mots de passe ne correspondent pas");
        }

        // Validation du rôle
        if (request.getRole() == null) {
            throw new RuntimeException("Le rôle est obligatoire");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        return userRepository.save(user);
    }


    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

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


//    public JwtResponse login(LoginRequest loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequest.getEmail(),
//                        loginRequest.getPassword()
//                )
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String jwt = jwtTokenProvider.generateToken(authentication);
//
//        User user = userRepository.findByEmail(loginRequest.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        return new JwtResponse(jwt, user.getId(), user.getEmail(), user.getRole());
//    }
//
//    @Transactional
//    public User register(RegisterRequest registerRequest) {
//        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
//            throw new RuntimeException("Passwords do not match");
//        }
//
//        if (userRepository.existsByEmail(registerRequest.getEmail())) {
//            throw new RuntimeException("Email is already taken");
//        }
//
//        User user = new User();
//        user.setName(registerRequest.getName());
//        user.setEmail(registerRequest.getEmail());
//        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
//        user.setRole(registerRequest.getRole());
//
//        return userRepository.save(user);
//    }
//
//    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
//        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found with this email"));
//
//        String token = UUID.randomUUID().toString();
//        user.setResetPasswordToken(token);
//        userRepository.save(user);
//
//        // Envoyer l'email avec le token
//        String resetLink = "http://your-frontend-url/reset-password?token=" + token;
//        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
//    }
//
//    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
//        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())) {
//            throw new RuntimeException("Passwords do not match");
//        }
//
//        User user = userRepository.findByResetPasswordToken(resetPasswordRequest.getToken())
//                .orElseThrow(() -> new RuntimeException("Invalid token"));
//
//        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
//        user.setResetPasswordToken(null);
//        userRepository.save(user);
//    }


}
