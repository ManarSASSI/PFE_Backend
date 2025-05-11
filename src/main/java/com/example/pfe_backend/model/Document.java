package com.example.pfe_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.*;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String type;
    private String cheminStockage;
    private LocalDate dateUpload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id")
    @JsonIgnore
    private Contrat contrat;

    public Document() {}

    public Document(Long id, String nom, String type, String cheminStockage, LocalDate dateUpload, Contrat contrat) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.cheminStockage = cheminStockage;
        this.dateUpload = dateUpload;
        this.contrat = contrat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCheminStockage() {
        return cheminStockage;
    }

    public void setCheminStockage(String cheminStockage) {
        this.cheminStockage = cheminStockage;
    }

    public LocalDate getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(LocalDate dateUpload) {
        this.dateUpload = dateUpload;
    }

    public Contrat getContrat() {
        return contrat;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
    }
}
