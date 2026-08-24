package com.example.backend_siop.dashboard.service;

import com.example.backend_siop.dashboard.dto.AnomalieCritiqueDTO;
import com.example.backend_siop.dashboard.dto.DashboardResponsableDTO;

import java.util.List;

public interface DashboardResponsableService {
    DashboardResponsableDTO getStatsResponsable();
    List<AnomalieCritiqueDTO> getAnomaliesCritiques();
}