package com.example.backend_siop.ascenseur.dto.composant;

import com.example.backend_siop.ascenseur.enums.TypeComposant;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ComposantDTO {
    private Long id;
    private String nom;
    private String reference;
    private String imageUrl;
    private TypeComposant type;
    private String fabricant;
    private LocalDate dateInstallation;
    private Integer dureeVieEstimeeMois;
    private boolean actif;
    private Long assemblageId;
    private String assemblageNom;
}