package com.example.backend_siop.tache.dto;

import com.example.backend_siop.tache.enums.Priorite;
import com.example.backend_siop.tache.enums.TypeTache;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TacheCreateDTO {

    @NotBlank
    private String titre;

    private String description;

    @NotNull
    private TypeTache type;

    @NotNull
    private Priorite priorite;

    private LocalDate dateEcheance;

    @NotNull
    private Long ascenseurId;

    @NotNull
    private Long responsableId;
}