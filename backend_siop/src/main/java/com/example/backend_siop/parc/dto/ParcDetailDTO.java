package com.example.backend_siop.parc.dto;

import com.example.backend_siop.ascenseur.dto.Site.SiteDTO;
import com.example.backend_siop.utilisateur.dto.UtilisateurResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ParcDetailDTO {

    private Long id;
    private String nom;
    private LocalDateTime createdAt;

    private List<SiteDTO> sites;
    private List<UtilisateurResponseDTO> techniciens;
}