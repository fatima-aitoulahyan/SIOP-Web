package com.example.backend_siop.dashboard.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardAdminDTO {

    // Section 1 — Structure du système
    private long nombreParcs;
    private long nombreSites;
    private long nombreAscenseurs;
    private long nombreClients;

    // Section 3 — Santé opérationnelle
    private long demandesTotales;
    private long demandesEnAttente;
    private long interventionsEnCours;
    private double tauxResolution;
    private long anomaliesCritiques;
}