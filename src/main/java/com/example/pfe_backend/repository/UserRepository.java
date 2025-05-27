package com.example.pfe_backend.repository;

import com.example.pfe_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<User> findByCreatedBy(User user);

    Optional<User> findByResetPasswordToken(String token);

    // Compter les partenaires
    long countByRole(User.Role role);

    // Trouver un partenaire par email
    Optional<User> findByEmailAndRole(String email, User.Role role);


    // Supprimer un partenaire
    void deleteByIdAndRole(Long id, User.Role role);

    List<User> findByRoleAndUsernameContainingIgnoreCase(User.Role role, String username);


    @Query("SELECT u FROM User u WHERE u.createdBy.id = :managerId")
    List<User> findByCreatedById(Long managerId);

    long countByCreatedById(Long managerId);

    @Query("SELECT u FROM User u WHERE u.createdBy.id = :managerId AND LOWER(u.username) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByCreatedByAndUsernameContaining(@Param("managerId") Long managerId, @Param("name") String name);

//    @Query("SELECT EXTRACT(MONTH FROM TO_TIMESTAMP(u.id / 1000)), COUNT(u) " +
//            "FROM User u " +
//            "WHERE u.role = 'PARTNER' AND u.createdBy.id = :managerId " +
//            "GROUP BY EXTRACT(MONTH FROM TO_TIMESTAMP(u.id / 1000))")
@Query(value = """
    SELECT EXTRACT(MONTH FROM TO_TIMESTAMP(u.id::numeric / 1000))::integer, 
           COUNT(u.id) 
    FROM users u 
    WHERE u.role = 'PARTNER' 
      AND u.created_by = :managerId 
    GROUP BY EXTRACT(MONTH FROM TO_TIMESTAMP(u.id::numeric / 1000))
    """, nativeQuery = true)
    List<Object[]> findMonthlyPartnerCounts(@Param("managerId") Long managerId);


    List<User> findByEnabled(boolean enabled);
}
