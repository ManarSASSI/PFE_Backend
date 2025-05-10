package com.example.pfe_backend.controller;

import com.example.pfe_backend.model.Contrat;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.repository.*;
import com.example.pfe_backend.service.ContratService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contrats")
public class ContratController {

    @Autowired
    private ContratService contratService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Contrat> getAllContrats() {
        return contratService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contrat> getContratById(@PathVariable Long id) {
        Contrat contrat = contratService.findById(id);
        if (contrat == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contrat);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Contrat> createContrat(@RequestBody Contrat contrat) {
        if (contrat.getPartnerId() == null) {
            throw new IllegalArgumentException("partnerId est requis");
        }

        User partner = userRepository.findById(contrat.getPartnerId())
                .orElseThrow(() -> new IllegalArgumentException("Partenaire non trouvé"));

        contrat.setPartner(partner);
        Contrat savedContrat = contratService.save(contrat);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedContrat);
//        Contrat savedContrat = contratService.save(contrat);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedContrat);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countContrats() {
        return ResponseEntity.ok(contratService.countContrats());
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Contrat> updateContrat(@PathVariable Long id,
                                                 @RequestBody Contrat contratDetails) {
        Contrat existingContrat = contratService.findById(id);
        if (existingContrat == null) {
            return ResponseEntity.notFound().build();
        }

        // Mise à jour des champs
        existingContrat.setTypeContrat(contratDetails.getTypeContrat());
        existingContrat.setObjetContrat(contratDetails.getObjetContrat());
        existingContrat.setMontant(contratDetails.getMontant());
        // Gestion du partenaire - version corrigée
        if (contratDetails.getPartner() != null) {
            User partner = userRepository.findById(contratDetails.getPartner().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Partenaire non trouvé"));
            existingContrat.setPartner(partner);
        }
//        existingContrat.setPartner(contratDetails.getPartner());
//        existingContrat.setPartnerId(contratDetails.getPartnerId());
        existingContrat.setDateDebut(contratDetails.getDateDebut());
        existingContrat.setDateFin(contratDetails.getDateFin());
        existingContrat.setStatus(contratDetails.getStatus());
        existingContrat.setCommentaire(contratDetails.getCommentaire());
        existingContrat.setDepartement(contratDetails.getDepartement());
        existingContrat.setHeureDebutSemaine(contratDetails.getHeureDebutSemaine());
        existingContrat.setHeureFinSemaine(contratDetails.getHeureFinSemaine());

        Contrat updatedContrat = contratService.save(existingContrat);
        return ResponseEntity.ok(updatedContrat);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteContrat(@PathVariable Long id) {
        Contrat contrat = contratService.findById(id);
        if (contrat == null) {
            return ResponseEntity.notFound().build();
        }
        contratService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/departement/{departement}")
    public List<Contrat> getContratsByDepartement(@PathVariable Contrat.Departement departement) {
        return contratService.findByDepartement(departement);
    }
}
