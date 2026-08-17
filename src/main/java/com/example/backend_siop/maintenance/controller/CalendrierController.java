package com.example.backend_siop.maintenance.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.maintenance.dto.CalendrierEventDTO;
import com.example.backend_siop.maintenance.service.CalendrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/calendrier")
@RequiredArgsConstructor
public class CalendrierController {

    private final CalendrierService calendrierService;

    @GetMapping
    public ApiResponse<List<CalendrierEventDTO>> getCalendrier(
            @RequestParam String debut,
            @RequestParam String fin,
            @RequestParam(required = false) Long technicienId
    ) {
        LocalDateTime dateDebut = parseDate(debut);
        LocalDateTime dateFin = parseDate(fin);

        List<CalendrierEventDTO> events =
                calendrierService.getEvenementsCalendrier(dateDebut, dateFin, technicienId);
        return ApiResponse.success(events);
    }

    /**
     * Accepte à la fois :
     * - un LocalDateTime ISO classique : "2026-07-14T18:51:01.823"
     * - un Instant UTC avec suffixe Z (envoyé par Angular via toISOString()) : "2026-07-14T18:51:01.823Z"
     */
    private LocalDateTime parseDate(String value) {
        try {
            if (value.endsWith("Z")) {
                return LocalDateTime.ofInstant(Instant.parse(value), ZoneId.systemDefault());
            }
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Format de date invalide pour '" + value + "'. Attendu : ISO-8601 (avec ou sans Z).", e);
        }
    }
}