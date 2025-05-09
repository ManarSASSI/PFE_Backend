package com.example.pfe_backend.repository;

import com.example.pfe_backend.model.SuiviContrat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuiviContratRepository extends JpaRepository<SuiviContrat, Long> {
    List<SuiviContrat> findByContratId(Long contratId);
}
