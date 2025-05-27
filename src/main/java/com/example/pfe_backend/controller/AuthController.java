package com.example.pfe_backend.controller;

import com.example.pfe_backend.DTO.*;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest loginRequest) {

        try {
            AuthResponse authResponse = authService.authenticate(loginRequest);

            // Créez une réponse standardisée
            Map<String, Object> response = new HashMap<>();
            response.put("token", authResponse.getToken());

            // Structurez les données utilisateur
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", authResponse.getUser().getId());
            userData.put("username", authResponse.getUser().getUsername());
            userData.put("email", authResponse.getUser().getEmail());
            userData.put("role", authResponse.getUser().getRole().name());
//            userData.put("avatar", authResponse.getUser().getAvatar());
            // Ajoutez d'autres champs si nécessaire

            response.put("user", userData);

            return ResponseEntity.ok(response);

        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Account awaiting validation by the administrator"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registerRequest) {
        try {
            User user = authService.register(registerRequest);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            authService.sendPasswordResetEmail(forgotPasswordRequest.getEmail());
            return ResponseEntity.ok("A reset email has been sent if the address exists.");
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        // 1. Invalide le token JWT si vous utilisez une blocklist
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // Ajoutez le token à une blocklist (implémentation nécessaire)
        }

        // 2. Retourne une réponse réussie
        return ResponseEntity.ok().body(Map.of(
                "message", "Logout successful"
        ));
    }

}