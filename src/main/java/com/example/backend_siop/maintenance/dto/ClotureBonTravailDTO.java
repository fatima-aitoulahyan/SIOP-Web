package com.example.backend_siop.maintenance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClotureBonTravailDTO {

    @NotBlank(message = "Le diagnostic est obligatoire")
    private String diagnostic;

    private String causeIdentifiee;

    @NotBlank(message = "L'action réalisée est obligatoire")
    private String actionRealisee;

    private String piecesRemplacees;

    private Boolean essaiConcluant;

    private String recommandations;
}
