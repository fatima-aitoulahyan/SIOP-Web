package com.example.backend_siop.maintenance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClotureChecklistDTO {

    @NotBlank(message = "Le bilan d'intervention est obligatoire")
    private String bilanIntervention;

    private boolean estMaintenance;
    private boolean estDepannage;
    private boolean estTravaux;
}