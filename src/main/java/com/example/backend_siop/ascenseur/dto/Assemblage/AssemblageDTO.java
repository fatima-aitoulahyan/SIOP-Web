package com.example.backend_siop.ascenseur.dto.Assemblage;

import com.example.backend_siop.ascenseur.enums.TypeAssemblage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AssemblageDTO {
    private Long id;
    private String nom;
    private String reference;
    private TypeAssemblage type;
    private String fabricant;
    private LocalDate dateInstallation;
    private Integer dureeVieEstimeeMois;
    private String description;
    private String imageUrl;
    private Integer niveau;
    private Long ascenseurId;
    private Long parentId;
    private String parentNom;
}