package com.example.backend_siop.utilisateur.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PhoneVerificationResponseDTO {

    private boolean exists;
    private Long userId;
    private String nom;
    private String prenom;
    private String telephone;
    private String role; // ADMINISTRATEUR, RESPONSABLE_MAINTENANCE, TECHNICIEN, CLIENT
    private boolean isInternal; // true si rôle interne (ADMIN, RESPONSABLE, TECHNICIEN)
}