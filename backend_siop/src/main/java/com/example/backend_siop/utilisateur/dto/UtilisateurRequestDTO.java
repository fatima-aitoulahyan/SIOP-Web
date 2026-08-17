package com.example.backend_siop.utilisateur.dto;

import com.example.backend_siop.utilisateur.enums.TypeUtilisateur;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UtilisateurRequestDTO {

    @NotBlank
    @Email
    private String email;

    private String telephone;

    @NotBlank
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères.")
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
}