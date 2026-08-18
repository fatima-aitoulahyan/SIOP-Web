package com.example.backend_siop.maintenance.entity;

import com.example.backend_siop.ascenseur.entity.Ascenseur;
import com.example.backend_siop.ascenseur.enums.TypeAscenseur;
import com.example.backend_siop.common.audit.Auditable;
import com.example.backend_siop.maintenance.enums.StatutEvaluation;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "evaluation_ascenseur")
@EqualsAndHashCode(callSuper = false, of = "id")
public class EvaluationAscenseur extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bon_travail_id", nullable = false, unique = true)
    private BonTravail bonTravail;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "technicien_id", nullable = false)
    private Technicien technicien;

    @Column(name = "date_visite")
    private LocalDateTime dateVisite;

    private String nom;

    private String fabricant;
    private String marque;
    private String modele;

    @Column(name = "numero_serie")
    private String numeroSerie;

    @Column(name = "code_barre")
    private String codeBarre;

    @Column(name = "nombre_etages")
    private Integer nombreEtages;

    @Column(name = "capacite_personnes")
    private Integer capacitePersonnes;

    @Column(name = "charge_max_kg")
    private Double chargeMaxKg;

    private Double vitesse;
    private String puissance;

    @Enumerated(EnumType.STRING)
    private TypeAscenseur type;

    @Column(name = "date_mise_en_service")
    private LocalDate dateMiseEnService;

    // --- bilan terrain ---
    @Column(name = "etat_portes")
    private String etatPortes;

    @Column(name = "position_cabine")
    private String positionCabine;

    @Column(columnDefinition = "TEXT")
    private String anomalies;

    @Column(name = "cause_exterieure", columnDefinition = "TEXT")
    private String causeExterieure;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEvaluation statut = StatutEvaluation.BROUILLON;

    @Column(name = "motif_refus", columnDefinition = "TEXT")
    private String motifRefus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Utilisateur responsable;

    @Column(name = "date_decision")
    private LocalDateTime dateDecision;

    // renseigné uniquement si accepté
    @OneToOne
    @JoinColumn(name = "ascenseur_cree_id")
    private Ascenseur ascenseurCree;
}