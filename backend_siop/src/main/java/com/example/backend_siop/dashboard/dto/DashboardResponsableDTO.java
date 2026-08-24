package com.example.backend_siop.dashboard.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardResponsableDTO {

    // période : mois en cours
    private long demandesCeMois;
    private long resoluesCeMois;

    // état actuel, sans limite de date
    private long enAttente;
    private long assignees;
    private long enCours;
    private long urgentesEnAttente;
    private long evaluationsAValider;
    private long nombreAnomaliesCritiques;

    // Techniciens
    private long techniciensEnInterventionAujourdhui;
    private long techniciensTotal;
}