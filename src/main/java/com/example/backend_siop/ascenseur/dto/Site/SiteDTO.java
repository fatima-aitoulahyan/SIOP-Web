package com.example.backend_siop.ascenseur.dto.Site;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteDTO {
    private Long id;
    private Long villeId;
    private String villeNom;
    private String villeCodePostal;
    private String villeRegion;
    private String adresse;
    private Long clientId;
    private String clientNom;
    private Long parcId;
    private String parcNom;
}