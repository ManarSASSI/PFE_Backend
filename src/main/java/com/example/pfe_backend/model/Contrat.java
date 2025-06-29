package com.example.pfe_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "contrats")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Contrat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeContrat typeContrat;

    @Column(nullable = false)
    private String objetContrat;

    private Double montant;

    @Column(name = "partner_id")
    private Long partnerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "partner_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    private User partner;

    @Column(name = "created_by", nullable = false)
    private Long createdById;


    @Column(nullable = false, name = "date_debut")
    private LocalDate dateDebut;

    @Column(nullable = false, name = "date_fin")
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    private StatusContrat status;

    private String commentaire;

    @Enumerated(EnumType.STRING)
    private Departement departement;

    private LocalTime heureDebutSemaine;
    private LocalTime heureFinSemaine;

    @Enumerated(EnumType.STRING)
    private EtatExecution etatExecution; // EN_COURS, TERMINE, EN_RETARD, etc.

    // Suivi des contrats
    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SuiviContrat> suivis = new ArrayList<>();

    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL)
    private List<Document> documents = new ArrayList<>();


    private Double penaliteParJour;
    private Integer joursRetard;
    private Double montantPenalite;
    private Boolean alerteExpirationEnvoyee = false;


    public Contrat() {
    }

    public Contrat(Long id, TypeContrat typeContrat, String objetContrat, Double montant, User partner, Long partnerId, Long createdBy, LocalDate dateDebut, LocalDate dateFin, StatusContrat status, String commentaire, Departement departement, LocalTime heureDebutSemaine, LocalTime heureFinSemaine, EtatExecution etatExecution, List<SuiviContrat> suivis, List<Document> documents, Double penaliteParJour, Integer joursRetard, Double montantPenalite, Boolean alerteExpirationEnvoyee) {
        this.id = id;
        this.typeContrat = typeContrat;
        this.objetContrat = objetContrat;
        this.montant = montant;
        this.partner = partner;
        this.partnerId = partnerId;
        this.createdById = createdBy;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.status = status;
        this.commentaire = commentaire;
        this.departement = departement;
        this.heureDebutSemaine = heureDebutSemaine;
        this.heureFinSemaine = heureFinSemaine;
        this.etatExecution = etatExecution;
        this.suivis = suivis;
        this.documents = documents;
        this.penaliteParJour = penaliteParJour;
        this.joursRetard = joursRetard;
        this.montantPenalite = montantPenalite;
        this.alerteExpirationEnvoyee = alerteExpirationEnvoyee;
    }


    @JsonProperty("partnerUsername")
    public String getPartnerUsername() {
        return (partner != null) ? partner.getUsername() : null;
    }

    public EtatExecution getEtatExecution() {
        return etatExecution;
    }

    public void setEtatExecution(EtatExecution etatExecution) {
        this.etatExecution = etatExecution;
    }

    public List<SuiviContrat> getSuivis() {
        return suivis;
    }

    public void setSuivis(List<SuiviContrat> suivis) {
        this.suivis = suivis;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeContrat getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(TypeContrat typeContrat) {
        this.typeContrat = typeContrat;
    }

    public String getObjetContrat() {
        return objetContrat;
    }

    public void setObjetContrat(String objetContrat) {
        this.objetContrat = objetContrat;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public User getPartner() {
        return partner;
    }

    public void setPartner(User partner) {
        this.partner= partner;
    }


    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public StatusContrat getStatus() {
        return status;
    }

    public void setStatus(StatusContrat status) {
        this.status = status;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public LocalTime getHeureDebutSemaine() {
        return heureDebutSemaine;
    }

    public void setHeureDebutSemaine(LocalTime heureDebutSemaine) {
        this.heureDebutSemaine = heureDebutSemaine;
    }

    public LocalTime getHeureFinSemaine() {
        return heureFinSemaine;
    }

    public void setHeureFinSemaine(LocalTime heureFinSemaine) {
        this.heureFinSemaine = heureFinSemaine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contrat contrat = (Contrat) o;
        return Objects.equals(id, contrat.id) && typeContrat == contrat.typeContrat && Objects.equals(objetContrat, contrat.objetContrat) && Objects.equals(montant, contrat.montant) && Objects.equals(partner, contrat.partner) && Objects.equals(dateDebut, contrat.dateDebut) && Objects.equals(dateFin, contrat.dateFin) && status == contrat.status && Objects.equals(commentaire, contrat.commentaire) && departement == contrat.departement && Objects.equals(heureDebutSemaine, contrat.heureDebutSemaine) && Objects.equals(heureFinSemaine, contrat.heureFinSemaine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, typeContrat, objetContrat, montant, partner, dateDebut, dateFin, status, commentaire, departement, heureDebutSemaine, heureFinSemaine);
    }

    @PrePersist
    @PreUpdate
    private void validatePartner() {
        if (partner != null && partner.getRole() != User.Role.PARTNER) {
            throw new IllegalArgumentException("Seuls les utilisateurs avec le rôle PARTNER peuvent être associés à un contrat");
        }
    }

    public Double getPenaliteParJour() {
        return penaliteParJour;
    }

    public void setPenaliteParJour(Double penaliteParJour) {
        this.penaliteParJour = penaliteParJour;
    }

    public Integer getJoursRetard() {
        return joursRetard;
    }

    public void setJoursRetard(Integer joursRetard) {
        this.joursRetard = joursRetard;
    }

    public Double getMontantPenalite() {
        return montantPenalite;
    }

    public void setMontantPenalite(Double montantPenalite) {
        this.montantPenalite = montantPenalite;
    }

    public Boolean getAlerteExpirationEnvoyee() {
        return alerteExpirationEnvoyee;
    }

    public void setAlerteExpirationEnvoyee(Boolean alerteExpirationEnvoyee) {
        this.alerteExpirationEnvoyee = alerteExpirationEnvoyee;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdBy) {
        this.createdById = createdBy;
    }

    public enum TypeContrat {
        SERVICE,
        TRAVAUX,
        CONTINU,
    }

    public enum StatusContrat {
        NOUVEAU,
        RENOUVELLEMENT,
    }

    public enum Departement {
        ADMIN_FINANCE,
        PPE_CC,
        SUPPLY_CHAIN,
        QUALITE
    }

    public enum EtatExecution {
        EN_COURS,
        TERMINE,
        EN_RETARD
    }




}
