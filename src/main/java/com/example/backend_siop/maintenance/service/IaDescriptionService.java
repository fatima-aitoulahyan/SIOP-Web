package com.example.backend_siop.maintenance.service;

import com.example.backend_siop.maintenance.entity.DemandeMaintenance;

public interface IaDescriptionService {
    String genererDescription(DemandeMaintenance demande);
}