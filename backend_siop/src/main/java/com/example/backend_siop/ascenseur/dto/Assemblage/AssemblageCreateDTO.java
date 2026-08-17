package com.example.backend_siop.ascenseur.dto.Assemblage;

import com.example.backend_siop.ascenseur.enums.TypeAssemblage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AssemblageCreateDTO {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "La référence est obligatoire")
    private String reference;

    @NotNull(message = "Le type est obligatoire")
    private TypeAssemblage type;

    private String fabricant;
    private LocalDate dateInstallation;
    private Integer dureeVieEstimeeMois;

    private String description;
    private String imageUrl;

    @NotNull(message = "L'ascenseur est obligatoire")
    private Long ascenseurId;

    private Long parentId;
}