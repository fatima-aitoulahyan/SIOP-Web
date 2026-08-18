package com.example.backend_siop.maintenance.dto;

import com.example.backend_siop.maintenance.enums.PrioriteDemande;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BonTravailIntegrationCreateDTO {

    // --- Option 1 : Ascenseur connu (recommandé) ---
    private Long ascenseurId;

    // --- Option 2 : Localisation libre (pour les cas d'urgence) ---
    @Size(max = 500, message = "L'adresse libre ne peut pas dépasser 500 caractères")
    private String adresseLibre;

    @Size(max = 100, message = "La ville libre ne peut pas dépasser 100 caractères")
    private String villeLibre;

    @Size(max = 200, message = "Le nom de l'ascenseur libre ne peut pas dépasser 200 caractères")
    private String nomAscenseurLibre;

    // --- Texte original du message WhatsApp (pour le technicien) ---
    @Size(max = 2000, message = "Le message original ne peut pas dépasser 2000 caractères")
    private String messageOriginal;

    // --- Priorité & urgence ---
    private boolean estUrgence = false;
    private PrioriteDemande priorite; // Si non renseigné et estUrgence=true => URGENTE

    // --- Informations obligatoires ---
    @NotNull(message = "La date d'intervention prévue est obligatoire")
    private LocalDateTime dateInterventionPrevue;

    @NotNull(message = "La durée estimée est obligatoire")
    private Integer dureeEstimeeMinutes;

    private Long technicienResponsableId;   // Optionnel : si absent, on prend le technicien de secours
    private List<Long> technicienIdsRenfort;

    private String description;
}