package com.example.pfe_backend.repository;

import com.example.pfe_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String Username);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email); // Trouver un user par email
    boolean existsByEmail(String email);
    boolean existsByUsername(String Usernamee);// Vérifier si un email existe déjà
    List<User> findByRole(User.Role role); // Trouver les users par rôle
    List<User> findByIdAndRole(Long id, User.Role role);

    Optional<User> findByResetPasswordToken(String token);

    // Compter les partenaires
    long countByRole(User.Role role);

    // Trouver un partenaire par email
    Optional<User> findByEmailAndRole(String email, User.Role role);


    // Supprimer un partenaire
    void deleteByIdAndRole(Long id, User.Role role);

}
