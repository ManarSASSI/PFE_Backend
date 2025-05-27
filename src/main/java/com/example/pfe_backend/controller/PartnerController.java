package com.example.pfe_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import com.example.pfe_backend.DTO.PartnerDto;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.repository.UserRepository;
import com.example.pfe_backend.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;

    private final UserRepository userRepository;

    private PartnerDto convertToPartnerDto(User user) {
        return new PartnerDto(
        user.getId(),
        user.getUsername(),
        user.getPhone(),
        user.getEmail(),
        user.getLocation());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartnerDto> getPartnerById(@PathVariable Long id) {
        User partner = partnerService.getPartnerById(id);
        return ResponseEntity.ok(convertToPartnerDto(partner));
    }

    @GetMapping
    public ResponseEntity<List<PartnerDto>> getAllPartners(@RequestParam(required = false) String name) {

        List<User> partners;
        if (name != null && !name.isEmpty()) {
            partners = userRepository.findByRoleAndUsernameContainingIgnoreCase(User.Role.PARTNER, name);
        } else {
            partners = userRepository.findByRole(User.Role.PARTNER);
        }

        List<PartnerDto> partnerDtos = partners.stream()
                .map(this::convertToPartnerDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(partnerDtos);
    }

    @PutMapping(value ="/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updatePartner(@PathVariable Long id, @RequestParam("username") String username,
                                              @RequestParam("email") String email,
                                              @RequestParam(value = "password", required = false) String password,
                                              @RequestParam(value = "phone", required = false) String phone,
                                              @RequestParam(value = "location", required = false) String location
//                                              @RequestParam(value = "avatar", required = false) MultipartFile avatar
    ) {
        User updatedPartner = partnerService.updatePartner(id, username, email, password, phone, location);
        return ResponseEntity.ok(updatedPartner);
    }


    @GetMapping("/count")
    public ResponseEntity<Long> countPartners() {
        return ResponseEntity.ok(partnerService.countPartners());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartner(@PathVariable Long id) {
        partnerService.deletePartner(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> createPartner(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "location", required = false) String location
//            @RequestParam(value = "avatar", required = false) MultipartFile avatar
    ) {

        User partner = new User();
        partner.setUsername(username);
        partner.setEmail(email);
        partner.setPassword(password); // Le service devrait encoder le mot de passe
        partner.setPhone(phone);
        partner.setLocation(location);
        partner.setRole(User.Role.PARTNER);

        // Gestion de l'avatar si présent
//        if (avatar != null && !avatar.isEmpty()) {
//            try {
//                String fileName = storeFile(avatar); // À implémenter
//                partner.setAvatar(fileName);
//            } catch (IOException e) {
//                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }

        User createdPartner = partnerService.createPartner(partner);
        return new ResponseEntity<>(createdPartner, HttpStatus.CREATED);
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<User>> getPartnersByManager(@PathVariable Long managerId, @RequestParam(required = false) String name) {
        List<User> partners;
        if (name != null && !name.isEmpty()) {
            partners = userRepository.findByCreatedByAndUsernameContaining(managerId, name);
        } else {
            partners = userRepository.findByCreatedById(managerId);
        }
        return ResponseEntity.ok(partners);
    }

    private String storeFile(MultipartFile file) throws IOException {
        String uploadDir = "uploads/partners/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = uploadDir + fileName;
        file.transferTo(new File(filePath));

        return fileName;
    }

    @GetMapping("/count/manager/{managerId}")
    public ResponseEntity<Long> countPartnersByManager(@PathVariable Long managerId) {
        return ResponseEntity.ok(userRepository.countByCreatedById(managerId));
    }

    @GetMapping("/monthly-count/{managerId}")
    public ResponseEntity<List<Integer>> getMonthlyPartnersCount(@PathVariable Long managerId) {
        List<Integer> counts = partnerService.getMonthlyPartnersCount(managerId);
        return ResponseEntity.ok(counts);
    }
}
