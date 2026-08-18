package com.example.backend_siop.maintenance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluationAscenseurValidationDto {
    private boolean accepter;
    private String motif;
}