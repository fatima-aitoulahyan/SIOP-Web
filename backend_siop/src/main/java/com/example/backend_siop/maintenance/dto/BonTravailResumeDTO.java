package com.example.backend_siop.maintenance.dto;

import com.example.backend_siop.maintenance.enums.PrioriteDemande;
import com.example.backend_siop.maintenance.enums.StatutBonTravail;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BonTravailResumeDTO {

    private Long id;
    private StatutBonTravail statut;
    private PrioriteDemande priorite;
    private LocalDateTime dateInterventionPrevue;

    private String ascenseurNom;
    private String siteAdresse;
    private String parcNom;

    private String technicienResponsableNom;

    // ==============================================================
    //  NOUVEAUX CHAMPS POUR L'INTÉGRATION
    // ==============================================================
    private String adresseLibre;
    private String villeLibre;
    private boolean estUrgence;
}