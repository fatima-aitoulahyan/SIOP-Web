package com.example.backend_siop.maintenance.dto;

import com.example.backend_siop.maintenance.enums.PrioriteDemande;
import com.example.backend_siop.maintenance.enums.TypeDemande;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DemandeMaintenanceCreateDTO {

    @NotNull(message = "L'ascenseur est obligatoire")
    private Long ascenseurId;

    @NotNull(message = "Le type de demande est obligatoire")
    private TypeDemande typeDemande;

    @NotNull(message = "La priorité est obligatoire")
    private PrioriteDemande priorite;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    private LocalDate dateSouhaitee;
}