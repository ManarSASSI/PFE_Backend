package com.example.pfe_backend.repository;

import com.example.pfe_backend.model.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.*;
import java.util.List;

public interface ContratRepository extends JpaRepository<Contrat, Long> {
    List<Contrat> findByDepartement(Contrat.Departement departement);
    List<Contrat> findByStatus(Contrat.StatusContrat status);
    List<Contrat> findByTypeContrat(Contrat.TypeContrat typeContrat);
    List<Contrat> findByDateFinBetween(LocalDate start, LocalDate end);
    @Query("SELECT c FROM Contrat c WHERE c.dateFin BETWEEN :start AND :end AND c.partner.id IS NOT NULL AND EXISTS (SELECT 1 FROM User u WHERE u.id = c.partner.id)")
    List<Contrat> findValidContractsWithPartners(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT c FROM Contrat c " +
            "WHERE c.dateFin BETWEEN :startDate AND :endDate " +
            "AND c.partner IS NOT NULL") // Utilisez directement la relation mappée
    List<Contrat> findContractsExpiringBetweenWithValidPartner(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<Contrat> findByDateFin(LocalDate dateFin);

    List<Contrat> findByEtatExecutionAndDateFinBefore(Contrat.EtatExecution etatExecution, LocalDate date);
    long countByCreatedById(Long managerId);
    List<Contrat> findByCreatedById(Long createdById);
}
