package com.example.backend_siop.ascenseur.dto.Site;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteUpdateDTO {

    private Long villeId;
    private Long parcId;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;
}