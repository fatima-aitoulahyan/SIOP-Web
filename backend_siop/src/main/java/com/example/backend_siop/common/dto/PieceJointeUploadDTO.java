package com.example.backend_siop.common.dto;

import com.example.backend_siop.common.enums.TypeEntiteJointe;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PieceJointeUploadDTO {
    private TypeEntiteJointe entiteType;
    private Long entiteId;
    private String description;
}