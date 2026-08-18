package com.example.backend_siop.maintenance.dto;

import com.example.backend_siop.maintenance.enums.PrioriteDemande;
import com.example.backend_siop.maintenance.enums.TypeDemande;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DemandeMaintenanceIntegrationCreateDTO {

    // --- Option 1 : Ascenseur connu ---
    private Long ascenseurId;

    // --- Option 2 : Adresse libre (pour les cas où l'ascenseur n'est pas identifié) ---
    @Size(max = 500)
    private String adresseLibre;

    @Size(max = 100)
    private String villeLibre;

    // --- Informations de la demande ---
    @NotNull(message = "Le type de demande est obligatoire")
    private TypeDemande typeDemande; // PANNE, ENTRETIEN_PREVENTIF, BRUIT_ANORMAL, EVALUATION, AUTRE

    @NotNull(message = "La priorité est obligatoire")
    private PrioriteDemande priorite;

    @Size(max = 2000)
    private String messageOriginal; // Le texte brut reçu de WhatsApp

    @NotNull(message = "La description est obligatoire")
    private String description;

    private LocalDate dateSouhaitee;
}