package com.example.backend_siop.maintenance.repository;

import com.example.backend_siop.maintenance.entity.EvaluationAscenseur;
import com.example.backend_siop.maintenance.enums.StatutEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluationAscenseurRepository extends JpaRepository<EvaluationAscenseur, Long> {

    Optional<EvaluationAscenseur> findByBonTravail_Id(Long bonTravailId);

    List<EvaluationAscenseur> findByStatut(StatutEvaluation statut);

    List<EvaluationAscenseur> findByTechnicien_Id(Long technicienId);

    List<EvaluationAscenseur> findByBonTravail_DemandeMaintenance_Client_Id(Long clientId);
    
    long countByStatut(StatutEvaluation statut);
}
