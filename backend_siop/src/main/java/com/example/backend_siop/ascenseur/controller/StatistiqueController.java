package com.example.backend_siop.statistiques.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistiques")
@RequiredArgsConstructor
public class StatistiqueController {

    // Injecte tes services ici (InterventionService, TechnicienService, SiteService)

    @GetMapping("/responsable")
    public ApiResponse<Map<String, Integer>> getStatistiquesResponsable() {
        Map<String, Integer> stats = new HashMap<>();

        // Remplace ces valeurs par de vraies requêtes à ta base
        stats.put("interventionsEnCours", 12); // interventionRepository.countByStatut("EN_COURS")
        stats.put("techniciensDisponibles", 5); // technicienRepository.countByDisponibleTrue()
        stats.put("urgencesSignalees", 3); // interventionRepository.countByUrgenceTrue()
        stats.put("sitesActifs", 24); // siteRepository.count()

        return ApiResponse.success(stats);
    }
}