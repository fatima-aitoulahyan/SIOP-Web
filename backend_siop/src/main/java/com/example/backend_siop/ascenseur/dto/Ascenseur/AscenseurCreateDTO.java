package com.example.backend_siop.ascenseur.dto.Ascenseur;

import com.example.backend_siop.ascenseur.enums.TypeAscenseur;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AscenseurCreateDTO {

    @NotNull(message = "Le client est obligatoire")
    private Long clientId;

    private Long siteId;

    @NotBlank(message = "Le nom de l'ascenseur est obligatoire")
    private String nom;

    private String description;

    @Positive(message = "Le coût d'acquisition doit être positif")
    private Double coutAcquisition;

    @NotBlank(message = "Le fabricant est obligatoire")
    private String fabricant;

    private String modele;

    @NotBlank(message = "La marque est obligatoire")
    private String marque;

    private String numeroSerie;

    private String codeBarre;

    private String puissance;

    @Positive(message = "Le nombre d'étages doit être positif")
    private Integer nombreEtages;

    @Positive(message = "La capacité en personnes doit être positive")
    private Integer capacitePersonnes;

    @Positive(message = "La charge maximale doit être positive")
    private Double chargeMaxKg;

    @Positive(message = "La vitesse doit être positive")
    private Double vitesse;

    private TypeAscenseur type;

    private LocalDate dateMiseEnService;

    private LocalDate dateExpirationGarantie;

    private String informationsSupplementaires;
}