package com.example.backend_siop.utilisateur.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String token;
    private String email;
    private String nom;
    private String prenom;
    private String type;
}