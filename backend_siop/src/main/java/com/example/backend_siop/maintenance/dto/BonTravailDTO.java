package com.example.backend_siop.maintenance.dto;

import com.example.backend_siop.common.dto.PieceJointeAvecUrlDTO;
import com.example.backend_siop.maintenance.enums.PrioriteDemande;
import com.example.backend_siop.maintenance.enums.StatutBonTravail;
import com.example.backend_siop.utilisateur.dto.TechnicienResumeDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BonTravailDTO {

    private Long id;
    private StatutBonTravail statut;
    private PrioriteDemande priorite;
    private LocalDateTime dateInterventionPrevue;
    private Integer dureeEstimeeMinutes;
    private String description;

    private LocalDateTime dateDebutReelle;
    private LocalDateTime dateFinReelle;

    private String diagnostic;
    private String causeIdentifiee;
    private String actionRealisee;
    private String piecesRemplacees;
    private Boolean essaiConcluant;
    private String recommandations;

    private Long demandeMaintenanceId;

    private Long ascenseurId;
    private String ascenseurNom;
    private String siteAdresse;
    private Long parcId;
    private String parcNom;

    private Long technicienResponsableId;
    private String technicienResponsableNom;

    private List<TechnicienResumeDTO> techniciens;

    private List<PieceJointeAvecUrlDTO> photosDemande;
    private List<PieceJointeAvecUrlDTO> piecesJointesBonTravail;

    private LocalDateTime createdAt;

    // ==============================================================
    //  NOUVEAUX CHAMPS POUR L'INTÉGRATION (n8n)
    // ==============================================================
    private String adresseLibre;
    private String villeLibre;
    private String nomAscenseurLibre;
    private String messageOriginal;
    private boolean estUrgence;
}