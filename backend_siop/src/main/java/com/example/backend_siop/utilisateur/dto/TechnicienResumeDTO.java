package com.example.backend_siop.utilisateur.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicienResumeDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String specialite;
}