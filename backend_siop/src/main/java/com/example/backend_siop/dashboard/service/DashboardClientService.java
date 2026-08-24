package com.example.backend_siop.dashboard.service;

import com.example.backend_siop.dashboard.dto.AscenseurAvecEtatDTO;
import com.example.backend_siop.dashboard.dto.DashboardClientDTO;
import com.example.backend_siop.dashboard.dto.DemandeSuiviDTO;
import com.example.backend_siop.utilisateur.entity.Client;

import java.util.List;

public interface DashboardClientService {
    DashboardClientDTO getStats(Client client);
    List<AscenseurAvecEtatDTO> getAscenseursAvecEtat(Client client);

    List<DemandeSuiviDTO> getSuiviDemandes(Client client);
}