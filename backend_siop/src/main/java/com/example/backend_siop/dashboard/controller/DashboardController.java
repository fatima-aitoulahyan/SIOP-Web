package com.example.backend_siop.dashboard.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.dashboard.dto.ActiviteParParcDTO;
import com.example.backend_siop.dashboard.dto.AnomalieCritiqueDTO;
import com.example.backend_siop.dashboard.dto.AscenseurAvecEtatDTO;
import com.example.backend_siop.maintenance.dto.BonTravailResumeDTO;
import com.example.backend_siop.dashboard.dto.DashboardAdminDTO;
import com.example.backend_siop.dashboard.dto.DashboardClientDTO;
import com.example.backend_siop.dashboard.dto.DashboardResponsableDTO;
import com.example.backend_siop.dashboard.dto.DashboardTechnicienDTO;
import com.example.backend_siop.dashboard.dto.DemandeSuiviDTO;
import com.example.backend_siop.dashboard.dto.PlanningJourDTO;
import com.example.backend_siop.dashboard.dto.ProchaineInterventionDTO;
import com.example.backend_siop.dashboard.dto.RepartitionUtilisateursDTO;
import com.example.backend_siop.dashboard.service.DashboardAdminService;
import com.example.backend_siop.dashboard.service.DashboardResponsableService;
import com.example.backend_siop.dashboard.service.DashboardTechnicienService;
import com.example.backend_siop.dashboard.service.DashboardClientService;
import com.example.backend_siop.utilisateur.entity.Client;
import com.example.backend_siop.utilisateur.entity.Technicien;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardResponsableService dashboardService;
    private final DashboardAdminService dashboardAdminService;
    private final DashboardTechnicienService dashboardTechnicienService;
    private final DashboardClientService dashboardClientService;

    // ── Responsable ──

    @GetMapping("/responsable/stats")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<DashboardResponsableDTO> getStatsResponsable() {
        return ApiResponse.success(dashboardService.getStatsResponsable());
    }

    @GetMapping("/responsable/anomalies-critiques")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<List<AnomalieCritiqueDTO>> getAnomaliesCritiques() {
        return ApiResponse.success(dashboardService.getAnomaliesCritiques());
    }

    // ── Admin ──

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ApiResponse<DashboardAdminDTO> getStatsAdmin() {
        return ApiResponse.success(dashboardAdminService.getStats());
    }

    @GetMapping("/admin/repartition-utilisateurs")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ApiResponse<RepartitionUtilisateursDTO> getRepartitionUtilisateurs() {
        return ApiResponse.success(dashboardAdminService.getRepartitionUtilisateurs());
    }

    @GetMapping("/admin/activite-par-parc")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ApiResponse<List<ActiviteParParcDTO>> getActiviteParParc() {
        return ApiResponse.success(dashboardAdminService.getActiviteParParc());
    }

    // ── Technicien ──

    @GetMapping("/technicien/stats")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ApiResponse<DashboardTechnicienDTO> getStatsTechnicien(
            @AuthenticationPrincipal Technicien technicien) {
        return ApiResponse.success(dashboardTechnicienService.getStats(technicien));
    }

    @GetMapping("/technicien/aujourd-hui")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ApiResponse<List<BonTravailResumeDTO>> getInterventionsAujourdhui(
            @AuthenticationPrincipal Technicien technicien) {
        return ApiResponse.success(dashboardTechnicienService.getInterventionsAujourdhui(technicien));
    }

    @GetMapping("/technicien/prochaine")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ApiResponse<ProchaineInterventionDTO> getProchaineIntervention(
            @AuthenticationPrincipal Technicien technicien) {
        return ApiResponse.success(dashboardTechnicienService.getProchaineIntervention(technicien));
    }

    @GetMapping("/technicien/planning-semaine")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ApiResponse<List<PlanningJourDTO>> getPlanningSemaine(
            @AuthenticationPrincipal Technicien technicien) {
        return ApiResponse.success(dashboardTechnicienService.getPlanningSemaine(technicien));
    }

    // ── Client ──

    @GetMapping("/client/stats")
    @PreAuthorize("hasRole('CLIENT')")
    public ApiResponse<DashboardClientDTO> getStatsClient(
             @AuthenticationPrincipal Client client) {
        return ApiResponse.success(dashboardClientService.getStats(client));
    }

    @GetMapping("/client/mes-ascenseurs-etat")
    @PreAuthorize("hasRole('CLIENT')")
    public ApiResponse<List<AscenseurAvecEtatDTO>> getAscenseursAvecEtat(
            @AuthenticationPrincipal Client client) {
        return ApiResponse.success(dashboardClientService.getAscenseursAvecEtat(client));
    }

    @GetMapping("/client/suivi")
    @PreAuthorize("hasRole('CLIENT')")
    public ApiResponse<List<DemandeSuiviDTO>> getSuiviDemandes(
            @AuthenticationPrincipal Client client) {
        return ApiResponse.success(dashboardClientService.getSuiviDemandes(client));
    }
}