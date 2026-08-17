package com.example.backend_siop.maintenance.dto;

import com.example.backend_siop.maintenance.enums.TypeEvenement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EvenementRequestDTO {

    @NotBlank
    private String titre;

    private String description;

    @NotNull
    private TypeEvenement type;

    @NotNull
    private LocalDateTime dateDebut;

    @NotNull
    private LocalDateTime dateFin;

    private String lieu;

    private List<Long> technicienIds;
}