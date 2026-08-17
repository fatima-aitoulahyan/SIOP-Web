package com.example.backend_siop.maintenance.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.maintenance.dto.EvenementRequestDTO;
import com.example.backend_siop.maintenance.entity.Evenement;
import com.example.backend_siop.maintenance.service.EvenementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evenements")
@RequiredArgsConstructor
public class EvenementController {

    private final EvenementService evenementService;

    @PostMapping
    public ApiResponse<Evenement> creer(
            @Valid @RequestBody EvenementRequestDTO dto,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        return ApiResponse.success(evenementService.creerEvenement(dto, userId));
    }

    @PutMapping("/{id}")
    public ApiResponse<Evenement> modifier(
            @PathVariable Long id,
            @Valid @RequestBody EvenementRequestDTO dto
    ) {
        return ApiResponse.success(evenementService.modifierEvenement(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> supprimer(@PathVariable Long id) {
        evenementService.supprimerEvenement(id);
        return ApiResponse.success(null);
    }
}