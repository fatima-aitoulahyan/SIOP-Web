package com.example.backend_siop.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PieceJointeResponseDTO {

    private Long id;
    private String nomFichier;
    private String url;
    private String typeFichier;
    private String description;
}