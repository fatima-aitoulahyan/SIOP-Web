package com.example.backend_siop.referentiel.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VilleResponseDTO {
    private Long id;
    private String nom;
    private String region;
    private String codePostal;
    private boolean active;
}