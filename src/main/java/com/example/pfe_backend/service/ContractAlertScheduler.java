package com.example.pfe_backend.service;

import com.example.pfe_backend.model.Alert;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.model.Contrat;
import com.example.pfe_backend.repository.ContratRepository;
import com.example.pfe_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class ContractAlertScheduler {
    @Autowired
    private ContratRepository contratRepository;
    @Autowired
    private AlertService alertService;


    @Transactional
    @Scheduled(cron = "0 */60 * * * *") // Toutes  minutes
//    @Scheduled(cron = "0 0 0 * * ?") // Exécution quotidienne à minuit
    public void checkContractExpirations() {
        log.info("Vérification des contrats expirants...");

        LocalDate now = LocalDate.now();
        LocalDate in30Days = now.plusDays(30);

        List<Contrat> contrats = contratRepository.findContractsExpiringBetweenWithValidPartner(now, in30Days);

        contrats.forEach(contrat -> {
            long daysRemaining = ChronoUnit.DAYS.between(now, contrat.getDateFin());
            String message = String.format(
                    "Le contrat '%s' expire dans %d jours",
                    contrat.getObjetContrat(),
                    daysRemaining
            );

            alertService.createAlert(
                    contrat.getId(),
                    contrat.getPartner().getId(),
                    message,
                    Alert.AlertType.CONTRAT_EXPIRATION
            );
        });
    }

    // Autres vérifications périodiques
    @Scheduled(cron = "0 0 9 * * ?") // Tous les jours à 9h
    public void checkPaymentDueDates() {
        // Implémentation similaire pour les paiements dus
    }
}

//    private void processContractExpiration(Contrat contrat, LocalDate now) {
//        try {
//            // Log complet pour le débogage
//            if (contrat.getPartner() == null) {
//                log.error("ERREUR: Contrat {} a un partenaire null", contrat.getId());
//                return;
//            }
//
//            User partner = contrat.getPartner(); // Utilisez directement l'objet mappé
//            long daysRemaining = ChronoUnit.DAYS.between(now, contrat.getDateFin());
//
//            String message = String.format(
//                    "Contrat '%s' (ID: %d) expire dans %d jours. Partenaire: %s",
//                    contrat.getObjetContrat(),
//                    contrat.getId(),
//                    daysRemaining,
//                    partner.getName()
//            );
//
//            alertService.createAlert(
//                    contrat.getId(),
//                    partner.getId(), // Utilisez l'ID du partenaire mappé
//                    message,
//                    Alert.AlertType.CONTRAT_EXPIRATION
//            );
//
//            log.info("Alerte créée pour le contrat ID: {}", contrat.getId());
//        } catch (Exception e) {
//            log.error("Échec du traitement du contrat ID {}: {}",
//                    contrat.getId(),
//                    e.getMessage());
//        }
//

//        log.info("🔔 Exécution du scheduler de vérification des contrats");
//
//        List<Contrat> expiringContracts = contratRepository
//                .findByDateFinBetween(LocalDate.now(), LocalDate.now().plusDays(30));
//
//        log.info("📋 {} contrats expirants trouvés", expiringContracts.size());
//
//        for (Contrat contrat : expiringContracts) {
//            long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), contrat.getDateFin());
//
//            String message = String.format(
//                    "Votre contrat %s expire dans %d jours. Veuillez prendre les dispositions nécessaires.",
//                    contrat.getId(),
//                    daysRemaining
//            );
//
//            log.info("⚠️ Création alerte pour contrat {} ({} jours restants)",
//                    contrat.getId(), daysRemaining);
//
//            alertService.createAlert(
//                    contrat.getId(),
//                    contrat.getPartnerId(),
//                    message,
//                    Alert.AlertType.CONTRAT_EXPIRATION
//            );
//        }

