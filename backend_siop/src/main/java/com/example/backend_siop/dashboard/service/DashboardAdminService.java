package com.example.backend_siop.dashboard.service;

import com.example.backend_siop.dashboard.dto.ActiviteParParcDTO;
import com.example.backend_siop.dashboard.dto.DashboardAdminDTO;
import com.example.backend_siop.dashboard.dto.RepartitionUtilisateursDTO;

import java.util.List;

public interface DashboardAdminService {
    DashboardAdminDTO getStats();
    RepartitionUtilisateursDTO getRepartitionUtilisateurs();
    List<ActiviteParParcDTO> getActiviteParParc();
}