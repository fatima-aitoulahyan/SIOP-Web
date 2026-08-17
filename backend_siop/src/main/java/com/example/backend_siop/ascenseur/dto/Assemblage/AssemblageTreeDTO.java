package com.example.backend_siop.ascenseur.dto.Assemblage;

import com.example.backend_siop.ascenseur.dto.composant.ComposantDTO;
import com.example.backend_siop.ascenseur.enums.TypeAssemblage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssemblageTreeDTO {
    private Long id;
    private String nom;
    private String reference;
    private TypeAssemblage type;
    private String description;
    private Integer niveau;
    private String imageUrl;
    private List<AssemblageTreeDTO> sousAssemblages;
    private List<ComposantDTO> composants;
}