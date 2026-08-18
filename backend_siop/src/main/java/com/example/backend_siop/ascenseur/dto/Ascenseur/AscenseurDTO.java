package com.example.backend_siop.ascenseur.dto.Ascenseur;

import com.example.backend_siop.common.dto.PieceJointeResponseDTO;
import com.example.backend_siop.ascenseur.enums.TypeAscenseur;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AscenseurDTO {

    private Long id;
    private String nom;
    private String clientPrenom;
    private String clientNom;
    private String description;
    private Double coutAcquisition;
    private String fabricant;
    private String modele;
    private String marque;
    private String numeroSerie;
    private String codeBarre;
    private String puissance;
    private Integer nombreEtages;
    private Integer capacitePersonnes;
    private Double chargeMaxKg;
    private Double vitesse;
    private TypeAscenseur type;
    private LocalDate dateMiseEnService;
    private LocalDate dateExpirationGarantie;
    private String informationsSupplementaires;
    private boolean actif;

    private Long clientId;
    private String clientNomEntreprise;

    private Long siteId;
    private String siteAdresse;

    private Long parcId;
    private String parcNom;

    private List<PieceJointeResponseDTO> piecesJointes;
}