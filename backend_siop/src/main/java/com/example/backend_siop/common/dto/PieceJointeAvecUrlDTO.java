package com.example.backend_siop.common.dto;

import com.example.backend_siop.common.enums.TypeFichier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PieceJointeAvecUrlDTO {
    private Long id;
    private String nomFichier;
    private String url;
    private TypeFichier typeFichier;
    private String description;
}