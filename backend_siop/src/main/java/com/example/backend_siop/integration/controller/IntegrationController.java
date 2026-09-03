package com.example.backend_siop.integration.controller;

import com.example.backend_siop.ascenseur.dto.AscenseurRechercheDTO;
import com.example.backend_siop.ascenseur.service.AscenseurService;
import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.maintenance.dto.BonTravailDTO;
import com.example.backend_siop.maintenance.dto.BonTravailIntegrationCreateDTO;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceDTO;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceIntegrationCreateDTO;
import com.example.backend_siop.maintenance.service.BonTravailService;
import com.example.backend_siop.maintenance.service.DemandeMaintenanceService;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import com.example.backend_siop.utilisateur.dto.PhoneVerificationResponseDTO;
import com.example.backend_siop.utilisateur.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
public class IntegrationController {

    private final BonTravailService bonTravailService;
    private final AscenseurService ascenseurService;
    private final DemandeMaintenanceService demandeMaintenanceService;
    private final UtilisateurService utilisateurService;

    /**
     * Endpoint de recherche floue d'ascenseurs.
     * Utilisé par n8n pour identifier un ascenseur à partir d'un texte libre (adresse, numéro de série, etc.)
     */
    @GetMapping("/ascenseurs/rechercher")
    public ApiResponse<List<AscenseurRechercheDTO>> rechercherAscenseurs(@RequestParam String query) {
        log.info("Recherche d'ascenseurs via intégration : query='{}'", query);
        return ApiResponse.success(ascenseurService.rechercherParTexteLibre(query));
    }

    /**
     * Endpoint de création d'intervention (Bon de Travail) pour n8n / WhatsApp.
     * Accepte soit un ascenseurId, soit une adresse libre + ville libre.
     */
    @PostMapping("/interventions")
    public ApiResponse<BonTravailDTO> creerIntervention(
            @Valid @RequestBody BonTravailIntegrationCreateDTO dto,
            @AuthenticationPrincipal Utilisateur systemUser) {

        log.info("Création d'intervention via intégration. Urgence={}, ascenseurId={}, adresseLibre='{}'",
                dto.isEstUrgence(), dto.getAscenseurId(), dto.getAdresseLibre());

        BonTravailDTO created = bonTravailService.creerIntegration(dto, systemUser);
        return ApiResponse.success("Intervention créée avec succès via l'intégration externe.", created);
    }

    @PostMapping("/demandes-maintenance")
public ApiResponse<DemandeMaintenanceDTO> creerDemandeMaintenance(
        @Valid @RequestBody DemandeMaintenanceIntegrationCreateDTO dto,
        @AuthenticationPrincipal Utilisateur systemUser) {

    log.info("Création d'une demande de maintenance via intégration. type={}, ascenseurId={}, adresseLibre='{}'",
            dto.getTypeDemande(), dto.getAscenseurId(), dto.getAdresseLibre());

    // Appel d'une nouvelle méthode du service (que nous allons créer)
    DemandeMaintenanceDTO created = demandeMaintenanceService.creerDepuisIntegration(dto, systemUser);
    return ApiResponse.success("Demande de maintenance créée avec succès.", created);
}

@GetMapping("/utilisateurs/verifier-phone")
public ApiResponse<PhoneVerificationResponseDTO> verifierPhone(@RequestParam String phoneNumber) {
    log.info("Vérification du numéro de téléphone via intégration : {}", phoneNumber);
    return ApiResponse.success(utilisateurService.verifierTelephone(phoneNumber));
}

}