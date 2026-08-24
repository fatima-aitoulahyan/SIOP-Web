package com.example.backend_siop.dashboard.dto;

import com.example.backend_siop.maintenance.dto.BonTravailResumeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProchaineInterventionDTO {
    private BonTravailResumeDTO intervention;  // null si aucune
    private String contexte;                    // "AUJOURDHUI", "DEMAIN", "AUCUNE"
    private boolean enRetard;                    // true si heure passée et non démarrée
}