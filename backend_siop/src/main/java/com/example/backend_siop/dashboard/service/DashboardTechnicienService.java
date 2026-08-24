package com.example.backend_siop.dashboard.service;

import com.example.backend_siop.maintenance.dto.BonTravailResumeDTO;
import com.example.backend_siop.dashboard.dto.DashboardTechnicienDTO;
import com.example.backend_siop.dashboard.dto.PlanningJourDTO;
import com.example.backend_siop.dashboard.dto.ProchaineInterventionDTO;
import com.example.backend_siop.utilisateur.entity.Technicien;

import java.util.List;

public interface DashboardTechnicienService {
    DashboardTechnicienDTO getStats(Technicien technicien);
    List<BonTravailResumeDTO> getInterventionsAujourdhui(Technicien technicien);
    ProchaineInterventionDTO getProchaineIntervention(Technicien technicien);
    List<PlanningJourDTO> getPlanningSemaine(Technicien technicien);
}