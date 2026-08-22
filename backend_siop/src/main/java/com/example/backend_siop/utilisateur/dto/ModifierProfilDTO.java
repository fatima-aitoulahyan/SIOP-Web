package com.example.backend_siop.utilisateur.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifierProfilDTO {

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    private String telephone;

    private String nomEntreprise;

    private String adresse;

    private String specialite;
}
