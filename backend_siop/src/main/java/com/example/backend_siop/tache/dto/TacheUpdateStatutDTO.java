package com.example.backend_siop.tache.dto;

import com.example.backend_siop.tache.enums.StatutTache;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TacheUpdateStatutDTO {
    @NotNull
    private StatutTache statut;
}