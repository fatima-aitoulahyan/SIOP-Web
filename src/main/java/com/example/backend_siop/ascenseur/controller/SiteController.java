package com.example.backend_siop.ascenseur.controller;

import com.example.backend_siop.ascenseur.dto.Site.SiteCreateDTO;
import com.example.backend_siop.ascenseur.dto.Site.SiteDTO;
import com.example.backend_siop.ascenseur.dto.Site.SiteUpdateDTO;
import com.example.backend_siop.ascenseur.service.SiteService;
import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.utilisateur.entity.Client;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    @PostMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<SiteDTO> creer(
            @Valid @RequestBody SiteCreateDTO dto,
            @AuthenticationPrincipal Utilisateur createur) {
        return ApiResponse.success(siteService.creer(dto, createur.getId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<SiteDTO> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal Utilisateur utilisateur) {
        return ApiResponse.success(siteService.getById(id, utilisateur));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<List<SiteDTO>> listerTous() {
        return ApiResponse.success(siteService.listerTous());
    }

    @GetMapping("/ville/{villeId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<List<SiteDTO>> listerParVille(@PathVariable Long villeId) {
        return ApiResponse.success(siteService.listerParVille(villeId));
    }

    @GetMapping("/client/{clientId}")
    public ApiResponse<List<SiteDTO>> listerParClient(
            @PathVariable Long clientId,
            @AuthenticationPrincipal Utilisateur utilisateur) {
        return ApiResponse.success(siteService.listerParClient(clientId, utilisateur));
    }

    @GetMapping("/mes-sites")
    @PreAuthorize("hasRole('CLIENT')")
    public ApiResponse<List<SiteDTO>> mesSites(@AuthenticationPrincipal Client client) {
        return ApiResponse.success(siteService.listerMesSites(client));
    }

    @GetMapping("/mon-parc")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ApiResponse<List<SiteDTO>> sitesDeMonParc(@AuthenticationPrincipal Technicien technicien) {
        return ApiResponse.success(siteService.listerSitesDeMonParc(technicien));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<SiteDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody SiteUpdateDTO dto) {
        return ApiResponse.success(siteService.modifier(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR','RESPONSABLE_MAINTENANCE')")
    public ApiResponse<Void> supprimer(@PathVariable Long id) {
        siteService.supprimer(id);
        return ApiResponse.success(null);
    }
}