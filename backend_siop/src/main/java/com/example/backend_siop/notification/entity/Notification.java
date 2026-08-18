package com.example.backend_siop.notification.entity;

import com.example.backend_siop.notification.enums.TypeNotification;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;

    @Enumerated(EnumType.STRING)
    private TypeNotification type;

    private String titre;

    @Column(length = 500)
    private String message;

    // Pour rediriger le front vers l'entité concernée (ex: "/bons-travail/12")
    private String entiteType; // "BON_TRAVAIL", "DEMANDE_MAINTENANCE", "EVALUATION"
    private Long entiteId;

    private boolean lu = false;

    private LocalDateTime dateCreation = LocalDateTime.now();
}