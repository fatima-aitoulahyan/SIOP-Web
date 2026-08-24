package com.example.backend_siop.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class DemandeSuiviDTO {
    private Long demandeId;
    private String ascenseurNom;
    private String typeDemande;
    private String statut;
    private String technicienNom;   // null si EN_ATTENTE (pas encore assigné)
    private LocalDate dateDemande;
}