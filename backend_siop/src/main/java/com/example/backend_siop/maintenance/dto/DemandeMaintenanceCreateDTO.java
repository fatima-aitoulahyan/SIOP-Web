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

    private Long ascenseurId; // ✅ Plus obligatoire (permet les demandes sans ascenseur)

    @NotNull(message = "Le type de demande est obligatoire")
    private TypeDemande typeDemande;

    @NotNull(message = "La priorité est obligatoire")
    private PrioriteDemande priorite;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    private LocalDate dateSouhaitee;

    // 🔥 Nouveaux champs pour l'intégration (adresse libre)
    private String villeSaisie;
    private String adresseSaisie;
}