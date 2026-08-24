package com.example.backend_siop.maintenance.service;

import com.example.backend_siop.maintenance.dto.DemandeEvaluationCreateDTO;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceCreateDTO;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceDTO;
import com.example.backend_siop.maintenance.dto.RejetDemandeDTO;
import com.example.backend_siop.maintenance.entity.DemandeMaintenance;
import com.example.backend_siop.maintenance.enums.StatutDemande;
import com.example.backend_siop.utilisateur.entity.Client;

import java.util.List;

public interface DemandeMaintenanceService {

    DemandeMaintenanceDTO creer(DemandeMaintenanceCreateDTO dto, Client client);


    DemandeMaintenanceDTO creerEvaluation(DemandeEvaluationCreateDTO dto, Client client);

    List<DemandeMaintenanceDTO> listerMesDemandes(Client client);

    DemandeMaintenanceDTO getDetail(Long id, Client client);

    DemandeMaintenanceDTO annuler(Long id, Client client);

    void verifierPeutAjouterPhoto(Long demandeId, Client client);

    List<DemandeMaintenanceDTO> listerDemandesEnAttente();

    List<DemandeMaintenanceDTO> listerToutesDemandes(StatutDemande statut);

    DemandeMaintenanceDTO getDetailPourResponsable(Long id);

    DemandeMaintenanceDTO rejeter(Long id, RejetDemandeDTO dto);

    DemandeMaintenance getEntitePourResponsable(Long id);

    DemandeMaintenanceDTO accepter(Long id);

    List<DemandeMaintenanceDTO> listerDemandesAtraiter();
}
