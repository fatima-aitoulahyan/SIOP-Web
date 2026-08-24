package com.example.backend_siop.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ActiviteParParcDTO {
    private Long parcId;
    private String parcNom;
    private long nombreSites;
    private long nombreTechniciens;
    private long demandesEnAttente;
}