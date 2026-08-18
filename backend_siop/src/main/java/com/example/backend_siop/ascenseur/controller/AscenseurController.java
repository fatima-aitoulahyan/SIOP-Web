package com.example.backend_siop.ascenseur.controller;

import com.example.backend_siop.ascenseur.dto.Ascenseur.AscenseurCreateDTO;
import com.example.backend_siop.ascenseur.dto.Ascenseur.AscenseurDTO;
import com.example.backend_siop.ascenseur.dto.Ascenseur.AscenseurUpdateDTO;
import com.example.backend_siop.ascenseur.service.AscenseurService;
import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.utilisateur.entity.Client;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ascenseurs")
@RequiredArgsConstructor
public class AscenseurController {

    private final AscenseurService ascenseurService;

    @PostMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<AscenseurDTO> creer(
            @Valid @RequestBody AscenseurCreateDTO dto,
            @AuthenticationPrincipal Utilisateur createur) {
        return ApiResponse.success(ascenseurService.creer(dto, createur.getId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<AscenseurDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(ascenseurService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<List<AscenseurDTO>> listerTous() {
        return ApiResponse.success(ascenseurService.listerTous());
    }

    @GetMapping("/client/{clientId}")
    public ApiResponse<List<AscenseurDTO>> listerParClient(@PathVariable Long clientId) {
         return ApiResponse.success(ascenseurService.listerParClient(clientId));
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/mes-ascenseurs")
    public ApiResponse<List<AscenseurDTO>> mesAscenseurs(@AuthenticationPrincipal Client client) {
        return ApiResponse.success(ascenseurService.listerParClient(client.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<AscenseurDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody AscenseurUpdateDTO dto) {
        return ApiResponse.success(ascenseurService.modifier(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<Void> supprimer(@PathVariable Long id) {
        ascenseurService.supprimer(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/site/{siteId}")
    public ApiResponse<List<AscenseurDTO>> listerParSite(
            @PathVariable Long siteId,
            @AuthenticationPrincipal Utilisateur utilisateur) {
        return ApiResponse.success(ascenseurService.listerParSite(siteId, utilisateur));
    }
    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<AscenseurDTO> changerStatut(@PathVariable Long id) {
        return ApiResponse.success(ascenseurService.basculerStatut(id));
    }
}