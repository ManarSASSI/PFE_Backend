package com.example.pfe_backend.service;

import com.example.pfe_backend.model.User;
import com.example.pfe_backend.repository.UserRepository;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllPartners() {
        return userRepository.findByRole(User.Role.PARTNER);
    }

    public long countPartners() {
        return userRepository.countByRole(User.Role.PARTNER);
    }

    public User getPartnerById(Long id) {

        List<User> partners = userRepository.findByIdAndRole(id, User.Role.PARTNER);
        if (partners.isEmpty()) {
            throw new RuntimeException("Partner not found");
        }
        if (partners.size() > 1) {
            throw new RuntimeException("Multiple partners found with the same ID");
        }
        return partners.get(0);
//        return userRepository.findByIdAndRole(id, User.Role.PARTNER)
//                .orElseThrow(() -> new RuntimeException("Partner not found"));
    }


    public void deletePartner(Long id) {
        userRepository.delete(getPartnerById(id));;
    }
    

    public User createPartner(User partner) {
        // Encoder le mot de passe avant de sauvegarder
        partner.setPassword(passwordEncoder.encode(partner.getPassword()));
        return userRepository.save(partner);
    }
}
