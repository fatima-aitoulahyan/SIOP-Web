package com.example.backend_siop.parc.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ParcDTO {

    private Long id;
    private String nom;
    private long nombreSites;
    private long nombreTechniciens;
    private LocalDateTime createdAt;
}