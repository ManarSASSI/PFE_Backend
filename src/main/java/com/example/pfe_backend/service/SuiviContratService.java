package com.example.pfe_backend.service;
import com.example.pfe_backend.model.Contrat;

import com.example.pfe_backend.model.*;
import com.example.pfe_backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@Transactional
public class SuiviContratService {

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private SuiviContratRepository suiviRepository;

    public SuiviContratService() {
    }

    public SuiviContratService(SuiviContratRepository suiviRepository, ContratRepository contratRepository) {
        this.suiviRepository = suiviRepository;
        this.contratRepository = contratRepository;
    }

    public void ajouterSuivi(Long contratId, SuiviContrat suivi) {
        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));

        suivi.setContrat(contrat);
        suivi.setDateSuivi(LocalDate.now());
        suiviRepository.save(suivi);

        // Mettre à jour le statut si nécessaire
        updateStatutContrat(contrat);
    }

    public List<SuiviContrat> getHistorique(Long contratId) {
        return suiviRepository.findByContratId(contratId);
    }

    private void updateStatutContrat(Contrat contrat) {
        // Logique de mise à jour du statut
    }

    public List<SuiviContrat> getAllSuiviContrats() {
        return suiviRepository.findAll();
    }
}
