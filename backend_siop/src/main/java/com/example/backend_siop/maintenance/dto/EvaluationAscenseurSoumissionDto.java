package com.example.backend_siop.maintenance.dto;

import com.example.backend_siop.ascenseur.enums.TypeAscenseur;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EvaluationAscenseurSoumissionDto {

    private String nom;
    @NotBlank(message = "Le fabricant est obligatoire")
    private String fabricant;

    private String marque;
    private String modele;
    private String numeroSerie;
    private String codeBarre;
    private Integer nombreEtages;
    private Integer capacitePersonnes;
    private Double chargeMaxKg;
    private Double vitesse;
    private String puissance;
    private TypeAscenseur type;
    private LocalDate dateMiseEnService;

    private String etatPortes;
    private String positionCabine;
    private String anomalies;
    private String causeExterieure;
    private String observations;
}