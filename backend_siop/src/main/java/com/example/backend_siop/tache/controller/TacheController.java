package com.example.backend_siop.tache.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.tache.dto.TacheCreateDTO;
import com.example.backend_siop.tache.dto.TacheDTO;
import com.example.backend_siop.tache.dto.TacheUpdateStatutDTO;
import com.example.backend_siop.tache.service.TacheService;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taches")
@RequiredArgsConstructor
public class TacheController {

    private final TacheService tacheService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR')")
    public ApiResponse<TacheDTO> creer(
            @Valid @RequestBody TacheCreateDTO dto,
            @AuthenticationPrincipal Utilisateur createur) {
        return ApiResponse.success(tacheService.creer(dto, createur.getId()));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE', 'ROLE_RESPONSABLE_MAINTENANCE')")
    public ApiResponse<List<TacheDTO>> listerToutes() {
        return ApiResponse.success(tacheService.listerToutes());
    }

    @GetMapping("/mes-taches")
    @PreAuthorize("hasAnyAuthority('RESPONSABLE_MAINTENANCE', 'ROLE_RESPONSABLE_MAINTENANCE', 'TECHNICIEN', 'ROLE_TECHNICIEN')")
    public ApiResponse<List<TacheDTO>> mesTaches(@AuthenticationPrincipal Utilisateur utilisateur) {
        if (utilisateur instanceof Technicien) {
            return ApiResponse.success(tacheService.listerParTechnicien(utilisateur.getId()));
        }
        return ApiResponse.success(tacheService.listerParResponsable(utilisateur.getId()));
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE', 'ROLE_RESPONSABLE_MAINTENANCE', 'TECHNICIEN', 'ROLE_TECHNICIEN')")
    public ApiResponse<TacheDTO> modifierStatut(
            @PathVariable Long id,
            @Valid @RequestBody TacheUpdateStatutDTO dto) {
        return ApiResponse.success(tacheService.modifierStatut(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR')")
    public ApiResponse<Void> supprimer(@PathVariable Long id) {
        tacheService.supprimer(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/assigner-techniciens")
    @PreAuthorize("hasAnyAuthority('RESPONSABLE_MAINTENANCE', 'ROLE_RESPONSABLE_MAINTENANCE')")
    public ApiResponse<TacheDTO> assignerTechniciens(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, List<Long>> body,
            @AuthenticationPrincipal Utilisateur responsable) {

        List<Long> technicienIds = body.get("technicienIds");
        if (technicienIds == null || technicienIds.isEmpty()) {
            throw new IllegalArgumentException("La liste des techniciens est requise");
        }

        return ApiResponse.success(tacheService.assignerTechniciens(id, technicienIds, responsable.getId()));
    }
}