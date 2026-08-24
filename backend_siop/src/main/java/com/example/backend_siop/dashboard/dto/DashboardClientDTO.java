package com.example.backend_siop.dashboard.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardClientDTO {
    private long nombreAscenseurs;
    private long demandesTotal;
    private long assignees;
    private long enCours;
    private long resolues;
}