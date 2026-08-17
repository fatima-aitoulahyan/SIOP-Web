package com.example.backend_siop.ascenseur.service;

import com.example.backend_siop.ascenseur.dto.Site.SiteCreateDTO;
import com.example.backend_siop.ascenseur.dto.Site.SiteDTO;
import com.example.backend_siop.ascenseur.dto.Site.SiteUpdateDTO;
import com.example.backend_siop.utilisateur.entity.Client;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.entity.Utilisateur;

import java.util.List;

public interface SiteService {
    SiteDTO creer(SiteCreateDTO dto, Long createurId);
    SiteDTO getById(Long id, Utilisateur utilisateur);
    SiteDTO modifier(Long id, SiteUpdateDTO dto);
    void supprimer(Long id);
    List<SiteDTO> listerTous();
    List<SiteDTO> listerParVille(Long villeId);
    List<SiteDTO> listerParClient(Long clientId, Utilisateur utilisateur);
    List<SiteDTO> listerMesSites(Client client);
    List<SiteDTO> listerSitesDeMonParc(Technicien technicien);
}