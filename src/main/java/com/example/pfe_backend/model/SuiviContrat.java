package com.example.pfe_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "suivi_contrats")
public class SuiviContrat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateSuivi;
    private String action;
    private String commentaire;

    private Contrat.EtatExecution etatExecution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id")
    @JsonIgnore
    private Contrat contrat;

    public SuiviContrat() {
    }

    public SuiviContrat(Long id, LocalDate dateSuivi, String action, String commentaire, Contrat contrat) {
        this.id = id;
        this.dateSuivi = dateSuivi;
        this.action = action;
        this.commentaire = commentaire;
        this.contrat = contrat;
    }

    public SuiviContrat(Long id, LocalDate dateSuivi, String action, String commentaire, Contrat.EtatExecution etatExecution, Contrat contrat) {
        this.id = id;
        this.dateSuivi = dateSuivi;
        this.action = action;
        this.commentaire = commentaire;
        this.etatExecution = etatExecution;
        this.contrat = contrat;
    }

    public Contrat.EtatExecution getEtatExecution() {
        return etatExecution;
    }

    public void setEtatExecution(Contrat.EtatExecution etatExecution) {
        this.etatExecution = etatExecution;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateSuivi() {
        return dateSuivi;
    }

    public void setDateSuivi(LocalDate dateSuivi) {
        this.dateSuivi = dateSuivi;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Contrat getContrat() {
        return contrat;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
    }
}
