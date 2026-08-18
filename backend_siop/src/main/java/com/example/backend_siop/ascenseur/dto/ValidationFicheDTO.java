package com.example.backend_siop.ascenseur.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ValidationFicheDTO {

    @NotNull(message = "Le client à associer est obligatoire pour valider la fiche")
    private Long clientId;
}