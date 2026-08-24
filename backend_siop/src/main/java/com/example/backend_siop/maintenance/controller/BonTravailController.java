package com.example.backend_siop.maintenance.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.maintenance.dto.BonTravailCreateDTO;
import com.example.backend_siop.maintenance.dto.BonTravailDTO;
import com.example.backend_siop.maintenance.dto.BonTravailResumeDTO;
import com.example.backend_siop.maintenance.dto.ConflitTechnicienDTO;
import com.example.backend_siop.maintenance.service.BonTravailService;
import com.example.backend_siop.utilisateur.dto.TechnicienResumeDTO;
import com.example.backend_siop.maintenance.dto.ClotureBonTravailDTO;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bons-travail")
@RequiredArgsConstructor
public class BonTravailController {

    private final BonTravailService bonTravailService;

    @PostMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE','ADMINISTRATEUR')")
    public ResponseEntity<ApiResponse<BonTravailDTO>> creer(
            @Valid @RequestBody BonTravailCreateDTO dto , @AuthenticationPrincipal Utilisateur utilisateur) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(bonTravailService.creer(dto ,utilisateur)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<List<BonTravailResumeDTO>> lister() {
        return ApiResponse.success(bonTravailService.lister());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<BonTravailDTO> getDetail(@PathVariable Long id) {
        return ApiResponse.success(bonTravailService.getDetail(id));
    }

    @GetMapping("/mes-interventions/{id}")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ApiResponse<BonTravailDTO> getDetailPourTechnicien(
        @PathVariable Long id,
        @AuthenticationPrincipal Technicien technicien) {
            return ApiResponse.success(bonTravailService.getDetailPourTechnicien(id, technicien));
    }

    @PatchMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE','ADMINISTRATEUR')")
    public ApiResponse<BonTravailDTO> annuler(@PathVariable Long id) {
        return ApiResponse.success(bonTravailService.annuler(id));
    }

    @GetMapping("/mes-interventions")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ApiResponse<List<BonTravailResumeDTO>> mesInterventions(
            @AuthenticationPrincipal Technicien technicien) {
        return ApiResponse.success(bonTravailService.listerMesInterventions(technicien));
    }

    @GetMapping("/verifier-disponibilite")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE','ADMINISTRATEUR')")
    public ApiResponse<List<ConflitTechnicienDTO>> verifierDisponibilite(
            @RequestParam List<Long> technicienIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam int dureeMinutes) {
        return ApiResponse.success(
                bonTravailService.verifierDisponibilite(technicienIds, debut, dureeMinutes));
    }

    @GetMapping("/techniciens-disponibles")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE','ADMINISTRATEUR')")
    public ApiResponse<List<TechnicienResumeDTO>> techniciensDisponibles(
            @RequestParam(required = false) Long ascenseurId,
            @RequestParam(required = false) Long siteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam int dureeMinutes) {

        if (ascenseurId != null) {
            return ApiResponse.success(
                    bonTravailService.listerTechniciensDisponibles(
                            ascenseurId,
                            debut,
                            dureeMinutes
                    )
            );
        }

        if (siteId != null) {
            return ApiResponse.success(
                    bonTravailService.listerTechniciensDisponiblesParSite(
                            siteId,
                            debut,
                            dureeMinutes
                    )
            );
        }

        throw new IllegalArgumentException(
                "ascenseurId ou siteId doit être fourni"
        );
    }
    
   

   @PatchMapping("/{id}/demarrer")
   @PreAuthorize("hasRole('TECHNICIEN')")
   public ApiResponse<BonTravailDTO> demarrer(
        @PathVariable Long id,
        @AuthenticationPrincipal Technicien technicien) {
    return ApiResponse.success(bonTravailService.demarrer(id, technicien));
   }

   @PatchMapping("/{id}/terminer")
   @PreAuthorize("hasRole('TECHNICIEN')")
   public ApiResponse<BonTravailDTO> terminer(
        @PathVariable Long id,
        @Valid @RequestBody ClotureBonTravailDTO dto) {
    return ApiResponse.success(bonTravailService.terminer(id, dto));
   }

    @GetMapping("/aujourd-hui")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<List<BonTravailResumeDTO>> interventionsAujourdhui() {
       return ApiResponse.success(bonTravailService.listerInterventionsAujourdhui());
    }
}
