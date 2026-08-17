package com.example.backend_siop.maintenance.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.maintenance.dto.EvaluationAscenseurDTO;
import com.example.backend_siop.maintenance.dto.EvaluationAscenseurSoumissionDto;
import com.example.backend_siop.maintenance.dto.EvaluationAscenseurValidationDto;
import com.example.backend_siop.maintenance.service.EvaluationAscenseurService;
import com.example.backend_siop.utilisateur.entity.Client;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations-ascenseur")
@RequiredArgsConstructor
public class EvaluationAscenseurController {

    private final EvaluationAscenseurService evaluationAscenseurService;

    @PostMapping("/bon-travail/{bonTravailId}")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ApiResponse<EvaluationAscenseurDTO> creerBrouillon(
            @PathVariable Long bonTravailId,
            @AuthenticationPrincipal Utilisateur technicien) {
        return ApiResponse.success(
                evaluationAscenseurService.creerBrouillon(bonTravailId, technicien.getId())
        );
    }

    @PutMapping("/{id}/soumettre")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ApiResponse<EvaluationAscenseurDTO> soumettre(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationAscenseurSoumissionDto dto,
            @AuthenticationPrincipal Utilisateur technicien) {
        return ApiResponse.success(
                evaluationAscenseurService.soumettre(id, dto, technicien.getId())
        );
    }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<EvaluationAscenseurDTO> valider(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationAscenseurValidationDto dto,
            @AuthenticationPrincipal Utilisateur responsable) {
        return ApiResponse.success(
                evaluationAscenseurService.valider(id, responsable.getId(), dto)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<EvaluationAscenseurDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(evaluationAscenseurService.getById(id));
    }

    @GetMapping("/bon-travail/{bonTravailId}")
    public ApiResponse<EvaluationAscenseurDTO> getByBonTravailId(@PathVariable Long bonTravailId) {
        return ApiResponse.success(evaluationAscenseurService.getByBonTravailId(bonTravailId));
    }

    @GetMapping("/en-attente")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<List<EvaluationAscenseurDTO>> getEnAttenteValidation() {
        return ApiResponse.success(evaluationAscenseurService.getEnAttenteValidation());
    }

    @GetMapping("/mes-evaluations")
    @PreAuthorize("hasAnyRole('TECHNICIEN', 'CLIENT')")
    public ApiResponse<List<EvaluationAscenseurDTO>> mesEvaluations(
            @AuthenticationPrincipal Utilisateur utilisateur) {
        if (utilisateur instanceof Client client) {
            return ApiResponse.success(evaluationAscenseurService.listerParClient(client.getId()));
        }
        return ApiResponse.success(evaluationAscenseurService.listerParTechnicien(utilisateur.getId()));
    }
}