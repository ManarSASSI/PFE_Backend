package com.example.pfe_backend.service;


import com.example.pfe_backend.model.Alert;
import com.example.pfe_backend.model.Contrat;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.repository.ContratRepository;
import com.example.pfe_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PenaltyService {

    private final ContratRepository contratRepository;
    private final AlertService alertService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(cron = "0 */2 * * * *")
    public void handleLateContracts() {
        LocalDate today = LocalDate.now();

        contratRepository.findByEtatExecutionAndDateFinBefore(
                Contrat.EtatExecution.EN_RETARD,
                today
        ).forEach(contrat -> {
            if (contrat.getPenaliteParJour() != null) {
                processPenalty(contrat, today);
            }
        });
    }

    private void processPenalty(Contrat contrat, LocalDate today) {
        // Calcul des jours de retard
        long joursRetard = ChronoUnit.DAYS.between(contrat.getDateFin(), today);

        // Mise à jour du contrat
        contrat.setJoursRetard((int) joursRetard);
        contrat.setMontantPenalite(contrat.getPenaliteParJour() * joursRetard);
        contratRepository.save(contrat);

        // Notification via AlertService existant
        notifyUsers(contrat, joursRetard);
    }

    private void notifyUsers(Contrat contrat, long joursRetard) {
        String message = String.format(
                "Pénalité appliquée : %.2f DN pour %d jours de retard sur le contrat %s",
                contrat.getMontantPenalite(),
                joursRetard,
                contrat.getId()
        );

        alertService.createAlert(
                contrat.getId(),
                contrat.getPartner().getId(),
                message,
                Alert.AlertType.CONTRAT_RETARD
        );


        userRepository.findByRole(User.Role.MANAGER).forEach(manager -> {
            alertService.createAlert(
                    contrat.getId(),
                    manager.getId(),
                    message,
                    Alert.AlertType.CONTRAT_RETARD
            );
        });
    }
}
