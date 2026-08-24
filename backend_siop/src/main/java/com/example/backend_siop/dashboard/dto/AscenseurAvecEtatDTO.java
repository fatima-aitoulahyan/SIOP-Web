package com.example.backend_siop.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AscenseurAvecEtatDTO {
    private Long ascenseurId;
    private String nom;
    private String siteAdresse;
    private String statutDemande;   // "EN_ATTENTE", "ASSIGNEE", "EN_COURS", ou null si aucune
    private boolean aDemandeActive;
}