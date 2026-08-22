package com.example.backend_siop.maintenance.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceCreateDTO;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceDTO;
import com.example.backend_siop.maintenance.dto.RejetDemandeDTO;
import com.example.backend_siop.maintenance.enums.StatutDemande;
import com.example.backend_siop.maintenance.service.DemandeMaintenanceService;
import com.example.backend_siop.maintenance.service.IaDescriptionService;
import com.example.backend_siop.maintenance.entity.DemandeMaintenance;
import com.example.backend_siop.utilisateur.entity.Client;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes-maintenance")
@RequiredArgsConstructor
public class DemandeMaintenanceController {

    private final DemandeMaintenanceService demandeService;
    private final IaDescriptionService iaDescriptionService;

    // ─── Côté Client ────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DemandeMaintenanceDTO>> creer(
            @Valid @RequestBody DemandeMaintenanceCreateDTO dto,
            @AuthenticationPrincipal Utilisateur utilisateur) {

        if (!(utilisateur instanceof Client client)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Accès réservé aux clients");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(demandeService.creer(dto, client)));
    }

    @GetMapping("/mes-demandes")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<DemandeMaintenanceDTO>> listerMesDemandes(
            @AuthenticationPrincipal Utilisateur utilisateur) {

        if (!(utilisateur instanceof Client client)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Accès réservé aux clients");
        }

        return ApiResponse.success(demandeService.listerMesDemandes(client));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<DemandeMaintenanceDTO> getDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal Utilisateur utilisateur) {

        if (!(utilisateur instanceof Client client)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Accès réservé aux clients");
        }

        return ApiResponse.success(demandeService.getDetail(id, client));
    }

    @PatchMapping("/{id}/annuler")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<DemandeMaintenanceDTO> annuler(
            @PathVariable Long id,
            @AuthenticationPrincipal Utilisateur utilisateur) {

        if (!(utilisateur instanceof Client client)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Accès réservé aux clients");
        }

        return ApiResponse.success(demandeService.annuler(id, client));
    }

    // ─── Côté Responsable Maintenance ───────────────────────────

    @GetMapping("/en-attente")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE', 'ROLE_RESPONSABLE_MAINTENANCE')")
    public ApiResponse<List<DemandeMaintenanceDTO>> listerDemandesEnAttente() {
        return ApiResponse.success(demandeService.listerDemandesEnAttente());
    }

    @GetMapping("/toutes")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE', 'ROLE_RESPONSABLE_MAINTENANCE')")
    public ApiResponse<List<DemandeMaintenanceDTO>> listerToutesDemandes(
            @RequestParam(required = false) StatutDemande statut) {
        return ApiResponse.success(demandeService.listerToutesDemandes(statut));
    }

    @GetMapping("/gestion/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE', 'ROLE_RESPONSABLE_MAINTENANCE')")
    public ApiResponse<DemandeMaintenanceDTO> getDetailPourResponsable(
            @PathVariable Long id) {
        return ApiResponse.success(demandeService.getDetailPourResponsable(id));
    }

    @PatchMapping("/{id}/rejeter")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE', 'ROLE_RESPONSABLE_MAINTENANCE')")
    public ApiResponse<DemandeMaintenanceDTO> rejeter(
            @PathVariable Long id,
            @Valid @RequestBody RejetDemandeDTO dto) {
        return ApiResponse.success(demandeService.rejeter(id, dto));
    }

    @GetMapping("/{id}/generer-description-ia")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE', 'ROLE_RESPONSABLE_MAINTENANCE')")
    public ApiResponse<String> genererDescriptionIa(@PathVariable Long id) {
        DemandeMaintenance demande = demandeService.getEntitePourResponsable(id);
        return ApiResponse.success(iaDescriptionService.genererDescription(demande));
    }


    @PostMapping("/evaluation")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DemandeMaintenanceDTO>> creerEvaluation(
            @Valid @RequestBody com.example.backend_siop.maintenance.dto.DemandeEvaluationCreateDTO dto,
            @AuthenticationPrincipal Utilisateur utilisateur) {

        if (!(utilisateur instanceof Client client)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Accès réservé aux clients");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(demandeService.creerEvaluation(dto, client)));
    }

    @PatchMapping("/{id}/accepter")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE', 'ROLE_RESPONSABLE_MAINTENANCE')")
    public ApiResponse<DemandeMaintenanceDTO> accepter(
            @PathVariable Long id) {
        return ApiResponse.success(demandeService.accepter(id));
    }

}