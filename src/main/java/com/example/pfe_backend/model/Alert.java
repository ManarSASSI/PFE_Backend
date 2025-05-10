package com.example.pfe_backend.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Contrat contrat;

    @ManyToOne
    private User recipient; // Le partenaire concerné

    private String message;
    private Date alertDate;
    private boolean isRead;

    @Enumerated(EnumType.ORDINAL)
    private AlertType type; // Enum pour les types d'alertes

    public Alert() {
    }

    public Alert(Long id, Contrat contrat, User recipient, String message, Date alertDate, boolean isRead, AlertType type) {
        this.id = id;
        this.contrat = contrat;
        this.recipient = recipient;
        this.message = message;
        this.alertDate = alertDate;
        this.isRead = isRead;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrat getContrat() {
        return contrat;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(Date alertDate) {
        this.alertDate = alertDate;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    public enum AlertType {
        CONTRAT_EXPIRATION,
        CONTRAT_RETARD,
        PAYMENT_DUE,
        DOCUMENT_MISSING
    }


}


