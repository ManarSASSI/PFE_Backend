package com.example.pfe_backend.repository;

import com.example.pfe_backend.model.Alert;
import com.example.pfe_backend.model.Contrat;
import com.example.pfe_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Contract;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByRecipientAndIsReadFalse(User user);
    List<Alert> findByContrat(Contrat contrat);
    List<Alert> findByRecipientIdOrderByAlertDateDesc(Long userId);
    List<Alert> findByContratId(Long contratId);
}
