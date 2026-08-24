package com.example.backend_siop.maintenance.repository;

import com.example.backend_siop.maintenance.entity.DemandeMaintenance;
import com.example.backend_siop.maintenance.enums.PrioriteDemande;
import com.example.backend_siop.maintenance.enums.StatutDemande;
import com.example.backend_siop.maintenance.enums.TypeDemande;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DemandeMaintenanceRepository extends JpaRepository<DemandeMaintenance, Long> {

    List<DemandeMaintenance> findByClientIdOrderByCreatedAtDesc(Long clientId);

    Optional<DemandeMaintenance> findByIdAndClientId(Long id, Long clientId);

    List<DemandeMaintenance> findByStatutOrderByPrioriteDescCreatedAtAsc(StatutDemande statut);

        // Bilans d'activité (mois en cours)
    long countByCreatedAtAfter(LocalDateTime debut);
    long countByStatutAndCreatedAtAfter(StatutDemande statut, LocalDateTime debut);
    long countByStatutAndDateResolutionAfter(StatutDemande statut, LocalDateTime depuis);

    // Files d'attente (état actuel)
    long countByStatut(StatutDemande statut);
    long countByTypeDemandeAndStatut(TypeDemande typeDemande, StatutDemande statut);
    long countByPrioriteAndStatut(PrioriteDemande priorite, StatutDemande statut);

    // Liste des demandes urgentes en attente (pour l'encart)
    List<DemandeMaintenance> findByPrioriteAndStatutOrderByCreatedAtAsc(
        PrioriteDemande priorite, StatutDemande statut);

    @Query("""
        SELECT d.ascenseur.siteEntity.parc.id, COUNT(d)
        FROM DemandeMaintenance d
        WHERE d.statut = :statut
          AND d.ascenseur IS NOT NULL
        GROUP BY d.ascenseur.siteEntity.parc.id
        """)
    List<Object[]> countEnAttenteGroupeParParc(@Param("statut") StatutDemande statut);
}