package com.example.backend_siop.maintenance.dto;

import com.example.backend_siop.common.dto.PieceJointeAvecUrlDTO;
import com.example.backend_siop.maintenance.enums.GraviteAnomalie;
import com.example.backend_siop.maintenance.enums.StatutItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemCheckListDTO {
    private Long id;
    private Integer ordre;
    private String libelle;
    private StatutItem statut;
    private GraviteAnomalie gravite;
    private String remarque;
    private List<PieceJointeAvecUrlDTO> piecesJointes;
}