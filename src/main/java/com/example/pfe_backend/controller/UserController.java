package com.example.pfe_backend.controller;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
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
}
