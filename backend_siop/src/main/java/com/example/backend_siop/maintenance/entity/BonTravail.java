package com.example.backend_siop.maintenance.entity;

import com.example.backend_siop.ascenseur.entity.Ascenseur;
import com.example.backend_siop.common.audit.Auditable;
import com.example.backend_siop.maintenance.enums.PrioriteDemande;
import com.example.backend_siop.maintenance.enums.StatutBonTravail;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bon_travail")
@EqualsAndHashCode(callSuper = false, of = "id")
public class BonTravail extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_maintenance_id", nullable = true, unique = true)
    private DemandeMaintenance demandeMaintenance;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "ascenseur_id", nullable = true)
    private Ascenseur ascenseur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "technicien_responsable_id", nullable = false)
    private Technicien technicienResponsable;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "bon_travail_technicien",
        joinColumns = @JoinColumn(name = "bon_travail_id"),
        inverseJoinColumns = @JoinColumn(name = "technicien_id")
    )
    private List<Technicien> techniciens = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutBonTravail statut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioriteDemande priorite;

    @Column(name = "date_intervention_prevue", nullable = false)
    private LocalDateTime dateInterventionPrevue;

    @Column(name = "duree_estimee_minutes", nullable = false)
    private Integer dureeEstimeeMinutes = 120;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_debut_reelle")
    private LocalDateTime dateDebutReelle;

    @Column(name = "date_fin_reelle")
    private LocalDateTime dateFinReelle;

    @Column(columnDefinition = "TEXT")
    private String diagnostic;

    @Column(name = "cause_identifiee", columnDefinition = "TEXT")
    private String causeIdentifiee;

    @Column(name = "action_realisee", columnDefinition = "TEXT")
    private String actionRealisee;

    @Column(name = "pieces_remplacees", columnDefinition = "TEXT")
    private String piecesRemplacees;

    @Column(name = "essai_concluant")
    private Boolean essaiConcluant;

    @Column(columnDefinition = "TEXT")
    private String recommandations;

    @OneToMany(mappedBy = "bonTravail", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<CommentaireBonTravail> commentaires = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "cree_par_id")
    private Utilisateur creePar;

    // ==============================================================
    //  NOUVEAUX CHAMPS POUR L'INTÉGRATION (WHATSAPP / n8n)
    // ==============================================================

    @Column(name = "adresse_libre", columnDefinition = "TEXT")
    private String adresseLibre;

    @Column(name = "ville_libre")
    private String villeLibre;

    @Column(name = "nom_ascenseur_libre")
    private String nomAscenseurLibre;

    @Column(name = "message_original", columnDefinition = "TEXT")
    private String messageOriginal;

    @Column(name = "est_urgence")
    private boolean estUrgence = false;
}