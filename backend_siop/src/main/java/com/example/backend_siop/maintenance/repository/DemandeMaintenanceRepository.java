package com.example.backend_siop.maintenance.repository;

import com.example.backend_siop.maintenance.entity.DemandeMaintenance;
import com.example.backend_siop.maintenance.enums.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DemandeMaintenanceRepository extends JpaRepository<DemandeMaintenance, Long> {

    List<DemandeMaintenance> findByClientIdOrderByCreatedAtDesc(Long clientId);

    Optional<DemandeMaintenance> findByIdAndClientId(Long id, Long clientId);

    List<DemandeMaintenance> findByStatutOrderByPrioriteDescCreatedAtAsc(StatutDemande statut);
}