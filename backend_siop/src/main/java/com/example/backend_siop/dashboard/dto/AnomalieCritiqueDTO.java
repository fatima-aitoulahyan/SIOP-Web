package com.example.backend_siop.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class AnomalieCritiqueDTO {
    private Long bonTravailId;
    private String ascenseurNom;
    private String siteAdresse;
    private String libelleItem;
    private String remarque;
    private LocalDate dateCloture;
}