package com.example.backend_siop.maintenance.dto;

import com.example.backend_siop.ascenseur.enums.TypeAscenseur;
import com.example.backend_siop.maintenance.enums.StatutEvaluation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class EvaluationAscenseurResponseDto {
    private Long id;
    private Long bonTravailId;
    private Long technicienId;
    private String technicienNom;

    private LocalDateTime dateVisite;

    private String nom;
    private String fabricant;
    private String marque;
    private String modele;
    private String numeroSerie;
    private String codeBarre;
    private Integer nombreEtages;
    private Integer capacitePersonnes;
    private Double chargeMaxKg;
    private Double vitesse;
    private String puissance;
    private TypeAscenseur type;
    private LocalDate dateMiseEnService;

    private String etatPortes;
    private String positionCabine;
    private String anomalies;
    private String causeExterieure;
    private String observations;

    private StatutEvaluation statut;
    private String motifRefus;
    private Long responsableId;
    private LocalDateTime dateDecision;
    private Long ascenseurCreeId;
}