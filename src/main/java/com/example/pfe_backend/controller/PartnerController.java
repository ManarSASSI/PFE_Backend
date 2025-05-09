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


    @GetMapping
    public ResponseEntity<List<PartnerDto>> getAllPartners() {
        List<User> partners = userRepository.findByRole(User.Role.PARTNER);

        List<PartnerDto> partnerDtos = partners.stream()
                .map(this::convertToPartnerDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(partnerDtos);
    }

    private PartnerDto convertToPartnerDto(User user) {
        return new PartnerDto(
        user.getId(),
        user.getUsername(),
        user.getPhone(),
        user.getEmail(),
        user.getLocation());
    }



    @GetMapping("/count")
    public ResponseEntity<Long> countPartners() {
        return ResponseEntity.ok(partnerService.countPartners());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
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
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {

        User partner = new User();
        partner.setUsername(username);
        partner.setEmail(email);
        partner.setPassword(password); // Le service devrait encoder le mot de passe
        partner.setPhone(phone);
        partner.setLocation(location);
        partner.setRole(User.Role.PARTNER);

        // Gestion de l'avatar si présent
        if (avatar != null && !avatar.isEmpty()) {
            try {
                String fileName = storeFile(avatar); // À implémenter
                partner.setAvatar(fileName);
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        User createdPartner = partnerService.createPartner(partner);
        return new ResponseEntity<>(createdPartner, HttpStatus.CREATED);
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
}
