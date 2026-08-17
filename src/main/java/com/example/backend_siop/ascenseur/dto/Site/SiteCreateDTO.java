package com.example.backend_siop.ascenseur.dto.Site;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteCreateDTO {

    @NotNull(message = "Le client est obligatoire")
    private Long clientId;

    @NotNull(message = "La ville est obligatoire")
    private Long villeId;

    @NotNull(message = "Le parc est obligatoire")
    private Long parcId;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;
}