package com.example.backend_siop.maintenance.service;

import com.example.backend_siop.maintenance.dto.EvenementRequestDTO;
import com.example.backend_siop.maintenance.entity.Evenement;

public interface EvenementService {

    Evenement creerEvenement(EvenementRequestDTO dto, Long creePar);

    Evenement modifierEvenement(Long id, EvenementRequestDTO dto);

    void supprimerEvenement(Long id);
}