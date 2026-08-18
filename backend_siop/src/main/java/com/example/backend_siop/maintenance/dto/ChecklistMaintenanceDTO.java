package com.example.backend_siop.maintenance.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ChecklistMaintenanceDTO {
    private Long id;
    private Integer mois;
    private Integer annee;

    private Long ascenseurId;
    private String ascenseurNom;

    private Long bonTravailId;

    private Long technicienId;
    private String technicienNom;

    private LocalTime heureArrivee;
    private LocalTime heureDepart;

    private boolean estMaintenance;
    private boolean estDepannage;
    private boolean estTravaux;

    private String bilanIntervention;

    private List<ItemCheckListDTO> items;
}