package com.example.pfe_backend.controller;

import com.example.pfe_backend.model.Contrat;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.repository.*;
import com.example.pfe_backend.service.ContratService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contrats")
public class ContratController {

    @Autowired
    private ContratService contratService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContratRepository contratRepository;

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
    public ResponseEntity<?> createContrat(@RequestBody Contrat contratRequest) {
        try {
            // Récupération du manager depuis l'ID dans la requête
            User manager = userRepository.findById(contratRequest.getCreatedById())
                    .orElseThrow(() -> new RuntimeException("Manager non trouvé avec l'ID: " + contratRequest.getCreatedById()));

            // Récupération du partenaire
            User partner = userRepository.findById(contratRequest.getPartnerId())
                    .orElseThrow(() -> new RuntimeException("Partenaire non trouvé"));

            // Association du manager
            contratRequest.setCreatedById(manager.getId());
            contratRequest.setPartner(partner);

            // Sauvegarde
            Contrat savedContrat = contratService.save(contratRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedContrat);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/count")
    public ResponseEntity<Long> countContrats() {
        return ResponseEntity.ok(contratService.countContrats());
    }

    @GetMapping("/count/manager/{managerId}")
    public ResponseEntity<Long> countContratsByManager(@PathVariable Long managerId) {
        return ResponseEntity.ok(contratService.countContratsByManager(managerId));
    }


    @PutMapping("/{id}")
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
        if (contratDetails.getPartner() != null) {
            User partner = userRepository.findById(contratDetails.getPartner().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Partenaire non trouvé"));
            existingContrat.setPartner(partner);
        }
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
//    @PreAuthorize("hasRole('ADMIN')")
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

    @PatchMapping("/{id}/etat-execution")
    public ResponseEntity<Contrat> updateEtatExecution(
            @PathVariable Long id,
            @RequestBody Map<String, Contrat.EtatExecution> update) {

        Contrat contrat = contratRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));

        contrat.setEtatExecution(update.get("etatExecution"));
        return ResponseEntity.ok(contratRepository.save(contrat));
    }

    @GetMapping("/manager/{managerId}")
    public List<Contrat> getContratsByManager(@PathVariable Long managerId) {
        return contratRepository.findByCreatedById(managerId);
    }



}
