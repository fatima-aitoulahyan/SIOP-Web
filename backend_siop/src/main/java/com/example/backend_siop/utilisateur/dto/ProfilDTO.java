package com.example.backend_siop.utilisateur.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProfilDTO {
    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private String role;
    private String telephone;
    private String nomEntreprise;
    private String adresse;
    private boolean actif;
    private LocalDateTime createdAt;
    private String photoUrl;
}
