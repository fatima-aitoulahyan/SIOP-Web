package com.example.backend_siop.tache.service;

import com.example.backend_siop.tache.dto.TacheCreateDTO;
import com.example.backend_siop.tache.dto.TacheDTO;
import com.example.backend_siop.tache.dto.TacheUpdateStatutDTO;

import java.util.List;

public interface TacheService {
    TacheDTO creer(TacheCreateDTO dto, Long createurId);
    TacheDTO getById(Long id);
    List<TacheDTO> listerToutes();
    List<TacheDTO> listerParResponsable(Long responsableId);

    List<TacheDTO> listerParTechnicien(Long technicienId);

    TacheDTO modifierStatut(Long id, TacheUpdateStatutDTO dto);
    TacheDTO assignerTechniciens(Long tacheId, List<Long> technicienIds, Long responsableId);
    void supprimer(Long id);
}