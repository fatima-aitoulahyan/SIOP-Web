package com.example.backend_siop.maintenance.dto;

import com.example.backend_siop.common.dto.PieceJointeAvecUrlDTO;
import com.example.backend_siop.maintenance.enums.PrioriteDemande;
import com.example.backend_siop.maintenance.enums.StatutDemande;
import com.example.backend_siop.maintenance.enums.TypeDemande;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DemandeMaintenanceDTO {

    private Long id;
    private TypeDemande typeDemande;
    private PrioriteDemande priorite;
    private StatutDemande statut;
    private String description;
    private LocalDate dateSouhaitee;
    private String motifRejet;

    private Long ascenseurId;
    private String ascenseurNom;

    // Nouveaux champs pour l'évaluation
    private String villeSaisie;
    private String adresseSaisie;

    private Long clientId;
    private String clientNom;

    private LocalDateTime createdAt;
    private LocalDateTime dateResolution;
    private List<PieceJointeAvecUrlDTO> photos;
}