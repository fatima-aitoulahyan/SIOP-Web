package com.example.backend_siop.ascenseur.service;

import com.example.backend_siop.ascenseur.dto.Ascenseur.AscenseurCreateDTO;
import com.example.backend_siop.ascenseur.dto.Ascenseur.AscenseurDTO;
import com.example.backend_siop.ascenseur.dto.Ascenseur.AscenseurUpdateDTO;
import com.example.backend_siop.utilisateur.entity.Utilisateur;

import java.util.List;

public interface AscenseurService {

    AscenseurDTO creer(AscenseurCreateDTO dto, Long createurId);

    AscenseurDTO modifier(Long id, AscenseurUpdateDTO dto);

    void supprimer(Long id);

    AscenseurDTO getById(Long id);

    List<AscenseurDTO> listerParClient(Long clientId);

    List<AscenseurDTO> listerTous();
    List<AscenseurDTO> listerParSite(Long siteId, Utilisateur utilisateur);
    AscenseurDTO basculerStatut(Long id);
}