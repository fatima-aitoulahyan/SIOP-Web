package com.example.backend_siop.referentiel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VilleCreateDTO {

    @NotBlank(message = "Le nom de la ville est obligatoire")
    private String nom;

    private String region;

    private String codePostal;
}