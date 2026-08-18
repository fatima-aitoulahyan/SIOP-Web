package com.example.backend_siop.maintenance.repository;

import com.example.backend_siop.maintenance.entity.ChecklistMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChecklistMaintenanceRepository extends JpaRepository<ChecklistMaintenance, Long> {

    Optional<ChecklistMaintenance> findByBonTravailId(Long bonTravailId);

    boolean existsByAscenseurIdAndMoisAndAnnee(Long ascenseurId, Integer mois, Integer annee);
}