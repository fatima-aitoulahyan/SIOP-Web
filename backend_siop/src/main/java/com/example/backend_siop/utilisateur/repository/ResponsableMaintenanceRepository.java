package com.example.backend_siop.utilisateur.repository;

import com.example.backend_siop.utilisateur.entity.ResponsableMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponsableMaintenanceRepository extends JpaRepository<ResponsableMaintenance, Long> {
}