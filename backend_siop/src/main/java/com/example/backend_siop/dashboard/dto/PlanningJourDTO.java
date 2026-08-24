package com.example.backend_siop.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class PlanningJourDTO {
    private LocalDate date;
    private String jourLabel;   // "Lun", "Mar"...
    private long nombreInterventions;
}