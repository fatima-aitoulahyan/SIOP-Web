package com.example.backend_siop.maintenance.service;

import com.example.backend_siop.maintenance.dto.DemandeEvaluationCreateDTO;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceCreateDTO;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceDTO;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceIntegrationCreateDTO;
import com.example.backend_siop.maintenance.dto.RejetDemandeDTO;
import com.example.backend_siop.maintenance.entity.DemandeMaintenance;
import com.example.backend_siop.maintenance.enums.StatutDemande;
import com.example.backend_siop.utilisateur.entity.Client;
import com.example.backend_siop.utilisateur.entity.Utilisateur;

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

    // 🔥 HEAD : méthode pour l'intégration n8n
    DemandeMaintenanceDTO creerDepuisIntegration(DemandeMaintenanceIntegrationCreateDTO dto, Utilisateur createur);

    // 🔥 deploy-dokploy : méthode pour accepter une demande
    DemandeMaintenanceDTO accepter(Long id);

    List<DemandeMaintenanceDTO> listerDemandesAtraiter();
}
