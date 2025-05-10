package com.example.pfe_backend.service;

import com.example.pfe_backend.model.Alert;
import com.example.pfe_backend.model.Contrat;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.repository.AlertRepository;
import com.example.pfe_backend.repository.ContratRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ContratService {

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private AlertRepository alertRepository;

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



}
