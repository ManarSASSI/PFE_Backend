package com.example.pfe_backend.service;

import com.example.pfe_backend.model.Alert;
import com.example.pfe_backend.model.Contrat;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.repository.AlertRepository;
import com.example.pfe_backend.repository.ContratRepository;
import com.example.pfe_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ContratService {

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserRepository userRepository;

    public ContratService(ContratRepository contratRepository, AlertRepository alertRepository) {
        this.contratRepository = contratRepository;
        this.alertRepository = alertRepository;
    }

    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    public Contrat findById(Long id) {
        return contratRepository.findById(id).orElse(null);
    }

    public Contrat save(Contrat contrat) {
        // Fetch the manager
        User manager = userRepository.findById(contrat.getCreatedById())
                .orElseThrow(() -> new IllegalStateException("Manager introuvable"));

        // Fetch the partner using the partnerId from the Contrat entity
        User partner = userRepository.findById(contrat.getPartner().getId())
                .orElseThrow(() -> new IllegalStateException("Partenaire introuvable"));

        // Check and set the partner's createdBy if necessary
        if (partner.getCreatedBy() == null) {
            partner.setCreatedBy(manager);
            userRepository.save(partner);
        }

        // Ensure the contrat references the updated partner
        contrat.setPartner(partner);

        return contratRepository.save(contrat);
    }

    public void delete(Long id) {

        List<Alert> alerts = alertRepository.findByContratId(id);
        alertRepository.deleteAll(alerts);

        contratRepository.deleteById(id);
    }

    public List<Contrat> findByDepartement(Contrat.Departement departement) {
        return contratRepository.findByDepartement(departement);
    }

    public List<Contrat> findByStatus(Contrat.StatusContrat status) {
        return contratRepository.findByStatus(status);
    }

    public long countContrats() {
        return contratRepository.count();
    }

    public long countContratsByManager(Long managerId) {
        return contratRepository.countByCreatedById(managerId);
    }



}
