package com.example.pfe_backend.service;

import com.example.pfe_backend.model.Contrat;
import com.example.pfe_backend.repository.ContratRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ContratService {

    @Autowired
    private ContratRepository contratRepository;

    public ContratService(ContratRepository contratRepository) {
        this.contratRepository = contratRepository;
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
        contratRepository.deleteById(id);
    }

    public List<Contrat> findByDepartement(Contrat.Departement departement) {
        return contratRepository.findByDepartement(departement);
    }

    public List<Contrat> findByStatus(Contrat.StatusContrat status) {
        return contratRepository.findByStatus(status);
    }



}
