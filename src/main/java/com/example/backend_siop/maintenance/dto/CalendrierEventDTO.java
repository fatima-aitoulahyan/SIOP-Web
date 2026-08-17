package com.example.backend_siop.maintenance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CalendrierEventDTO {

    private String id;
    private String titre;
    private String source;
    private String type;
    private LocalDateTime debut;
    private LocalDateTime fin;
    private String lieu;
    private List<Long> technicienIds;
    private List<String> technicienNoms;
    private String couleur;
}