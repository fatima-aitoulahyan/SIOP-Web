package com.example.backend_siop.maintenance.dto;

import com.example.backend_siop.maintenance.enums.GraviteAnomalie;
import com.example.backend_siop.maintenance.enums.StatutItem;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCheckListUpdateDTO {

    @NotNull(message = "Le statut est obligatoire")
    private StatutItem statut;

    private GraviteAnomalie gravite;
    private String remarque;
}