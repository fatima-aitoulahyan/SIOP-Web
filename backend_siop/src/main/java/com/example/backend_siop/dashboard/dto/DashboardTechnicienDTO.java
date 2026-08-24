package com.example.backend_siop.dashboard.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardTechnicienDTO {
    private long interventionsAujourdhui;
    private long enCours;
    private long totalCeMois;
    private long termineesCeMois;
}