package com.example.backend_siop.ascenseur.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AscenseurRechercheDTO {

    private Long id;
    private String nom;
    private String adresseComplete;
    private String ville;
    private String numeroSerie;
    private Integer score; // 0 à 100, pour trier les résultats
}