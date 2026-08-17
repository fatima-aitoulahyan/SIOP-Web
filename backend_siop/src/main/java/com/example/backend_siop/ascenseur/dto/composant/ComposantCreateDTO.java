package com.example.backend_siop.ascenseur.dto.composant;

import com.example.backend_siop.ascenseur.enums.TypeComposant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ComposantCreateDTO {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "La référence est obligatoire")
    private String reference;

    @NotNull(message = "Le type est obligatoire")
    private TypeComposant type;

    private String fabricant;

    private String imageUrl;

    private LocalDate dateInstallation;

    private Integer dureeVieEstimeeMois;

    @NotNull(message = "L'assemblage est obligatoire")
    private Long assemblageId;
}