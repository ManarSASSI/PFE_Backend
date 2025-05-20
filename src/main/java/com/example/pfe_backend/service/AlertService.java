package com.example.pfe_backend.service;

import com.example.pfe_backend.model.*;
import com.example.pfe_backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@Transactional
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private EmailService emailService;


    public void createAlert(Long contratId, Long partnerId, String message, Alert.AlertType type) {
        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(() -> new IllegalArgumentException("Contrat non trouvé"));

        User partner = userRepository.findById(partnerId)
                .orElseThrow(() -> new IllegalArgumentException("Partenaire non trouvé"));

        Alert alert = new Alert();
        alert.setContrat(contrat);
        alert.setRecipient(partner);
        alert.setMessage(message);
        alert.setAlertDate(new Date());
        alert.setRead(false);
        alert.setType(type);

        alertRepository.save(alert);


        emailService.sendAlertEmail(partner, message, type);

        log.info("Alerte créée pour le contrat ID {} et le partenaire {}", contratId, partnerId);
    }


    public List<Alert> getAlertsForUser(Long userId) {
        return alertRepository.findByRecipientIdOrderByAlertDateDesc(userId);
    }

    public List<Alert> getUnreadAlertsForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return alertRepository.findByRecipientAndIsReadFalse(user);
    }

    // Marquer une alerte comme lue
    public void markAsRead(Long alertId) {
        Alert alert = alertRepository.findById(alertId).orElseThrow();
        alert.setRead(true);
        alertRepository.save(alert);
    }

    public void deleteAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alerte non trouvée avec l'ID : " + alertId));

        alertRepository.delete(alert);
        log.info("Alerte supprimée avec l'ID : {}", alertId);
    }

    public List<Alert> getAlertsForManager(Long managerId) {
        return alertRepository.findAlertsByManager(managerId);
    }

}
