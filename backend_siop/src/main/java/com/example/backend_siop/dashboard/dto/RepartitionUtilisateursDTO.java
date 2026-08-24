package com.example.backend_siop.dashboard.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RepartitionUtilisateursDTO {
    private long clients;
    private long techniciens;
    private long responsables;
    private long administrateurs;
    private long total;
}