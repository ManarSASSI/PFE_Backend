package com.example.pfe_backend.controller;
import com.example.pfe_backend.DTO.UpdateUserRequest;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Créer un utilisateur
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // Récupérer tous les utilisateurs
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Récupérer un utilisateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // Mettre à jour un utilisateur
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest updateRequest)
    {
        System.out.println("Received UpdateUserRequest: " + updateRequest);
        try {

            User updatedUser = userService.updateUser(id, updateRequest);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process avatar file: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {


        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Récupérer les utilisateurs par rôle (ex: /api/users/role/ADMIN)
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable User.Role role) {
        List<User> users = userService.getUsersByRole(role);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @GetMapping("/pending")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getPendingUsers() {
        return ResponseEntity.ok(userService.getPendingUsers());
    }

    @PostMapping("/{id}/approve")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> approveUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.approveUser(id));
    }

    @GetMapping("/pending/count")
    public ResponseEntity<Integer> getPendingUsersCount() {
        return ResponseEntity.ok(userService.getPendingUsers().size());
    }

//    @GetMapping("/{id}/avatar")
//    public ResponseEntity<byte[]> getAvatar(@PathVariable Long id) {
//        try {
//            User user = userService.getUserById(id);
//
//            // Vérifier la présence des données
//            if(user.getAvatarData() == null || user.getAvatarData().length == 0) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//            }
//
//            MediaType mediaType = MediaType.parseMediaType(user.getAvatarType());
//
//            return ResponseEntity.ok()
//                    .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
//                    .contentType(mediaType)
//                    .body(user.getAvatarData());
//
//        } catch (InvalidMimeTypeException e) {
//            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
////
//    }

}
