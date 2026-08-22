package com.example.backend_siop.utilisateur.dto;

import com.example.backend_siop.utilisateur.enums.TypeUtilisateur;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UtilisateurRequestDTO {

    @NotBlank
    @Email
    private String email;

    private String telephone;

    private String motDePasse;

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    private String nomEntreprise;

    @NotNull
    private TypeUtilisateur type;

    private String adresse;
    private String specialite;
    private Boolean actif = true;

    private List<Long> parcIds;
}