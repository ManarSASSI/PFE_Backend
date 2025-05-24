package com.example.pfe_backend.service;

import com.example.pfe_backend.model.User;
import com.example.pfe_backend.repository.UserRepository;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;



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

    public User updatePartner(Long id, String username, String email, String password,
                              String phone, String location, MultipartFile avatar) {
        User partner = getPartnerById(id);
        partner.setUsername(username);
        partner.setEmail(email);

        if (password != null && !password.isEmpty()) {
            partner.setPassword(passwordEncoder.encode(password));
        }

        partner.setPhone(phone);
        partner.setLocation(location);

        if (avatar != null && !avatar.isEmpty()) {
            String fileName = fileStorageService.storeFile(avatar);
            partner.setAvatar(fileName);
        }
        return userRepository.save(partner);
    }

    public List<Integer> getMonthlyPartnersCount(Long managerId) {
        List<Object[]> results = userRepository.findMonthlyPartnerCounts(managerId);
        int[] monthlyCounts = new int[12];

        for (Object[] result : results) {
            int month = (int) result[0];
            Long count = (Long) result[1];
            monthlyCounts[month - 1] = count.intValue();
        }

        return Arrays.stream(monthlyCounts).boxed().collect(Collectors.toList());
    }

}
