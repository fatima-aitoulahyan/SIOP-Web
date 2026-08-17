package com.example.backend_siop.utilisateur.dto;

import com.example.backend_siop.utilisateur.enums.TypeUtilisateur;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UtilisateurResponseDTO {
    private Long id;
    private String email;
    private String telephone;
    private String nom;
    private String prenom;
    private String nomEntreprise;
    private boolean actif;
    private TypeUtilisateur type;
    private String adresse;
    private String specialite;
    private LocalDateTime createdAt;
}