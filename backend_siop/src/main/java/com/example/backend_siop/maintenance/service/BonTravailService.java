package com.example.backend_siop.maintenance.service;

import com.example.backend_siop.maintenance.dto.BonTravailCreateDTO;
import com.example.backend_siop.maintenance.dto.BonTravailDTO;
import com.example.backend_siop.maintenance.dto.BonTravailResumeDTO;
import com.example.backend_siop.maintenance.dto.ConflitTechnicienDTO;
import com.example.backend_siop.utilisateur.dto.TechnicienResumeDTO;
import com.example.backend_siop.maintenance.dto.ClotureBonTravailDTO;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.entity.Utilisateur;

import java.time.LocalDateTime;
import java.util.List;

public interface BonTravailService {

    BonTravailDTO creer(BonTravailCreateDTO dto, Utilisateur creePar);

    List<BonTravailResumeDTO> lister();

    BonTravailDTO getDetail(Long id);

    BonTravailDTO annuler(Long id);

    List<BonTravailResumeDTO> listerMesInterventions(Technicien technicien);

    BonTravailDTO getDetailPourTechnicien(Long id, Technicien technicien);

    List<ConflitTechnicienDTO> verifierDisponibilite(
            List<Long> technicienIds, LocalDateTime debut, int dureeMinutes);

    List<TechnicienResumeDTO> listerTechniciensDisponibles(
            Long ascenseurId, LocalDateTime debut, int dureeMinutes);
            
   
    BonTravailDTO demarrer(Long id, Technicien technicien);
    
    BonTravailDTO terminer(Long id, ClotureBonTravailDTO dto);

    List<TechnicienResumeDTO> listerTechniciensDisponiblesParSite(
            Long siteId,
            LocalDateTime debut,
            int dureeMinutes
    );
}
