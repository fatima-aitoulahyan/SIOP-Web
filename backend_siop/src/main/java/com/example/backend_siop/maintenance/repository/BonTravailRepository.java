package com.example.backend_siop.maintenance.repository;

import com.example.backend_siop.maintenance.entity.BonTravail;
import com.example.backend_siop.maintenance.enums.StatutBonTravail;
import com.example.backend_siop.utilisateur.entity.Technicien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BonTravailRepository extends JpaRepository<BonTravail, Long> {

    List<BonTravail> findAllByOrderByStatutAscDateInterventionPrevueAsc();

    List<BonTravail> findByTechnicienResponsableId(Long technicienId);

    List<BonTravail> findByTechniciensContaining(Technicien technicien);

    List<BonTravail> findByTechniciensContainingAndStatutIn(
            Technicien technicien,
            Collection<StatutBonTravail> statuts);

    @Query("""
        SELECT DISTINCT bt FROM BonTravail bt
        LEFT JOIN FETCH bt.techniciens
        LEFT JOIN FETCH bt.technicienResponsable
        LEFT JOIN FETCH bt.ascenseur
        WHERE bt.dateInterventionPrevue BETWEEN :debut AND :fin
        AND (:technicienId IS NULL
             OR bt.technicienResponsable.id = :technicienId
             OR EXISTS (SELECT 1 FROM bt.techniciens t WHERE t.id = :technicienId))
        ORDER BY bt.dateInterventionPrevue ASC
        """)
    List<BonTravail> findBonsTravailDansPeriode(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin,
            @Param("technicienId") Long technicienId
    );

        // Comptage par statut
    long countByStatut(StatutBonTravail statut);

    // Bons de travail d'aujourd'hui (entre début et fin de journée)
    List<BonTravail> findByDateInterventionPrevueBetweenOrderByDateInterventionPrevueAsc(
        LocalDateTime debutJour, LocalDateTime finJour);
    
    // Interventions d'un technicien dans une période (aujourd'hui, semaine)
    List<BonTravail> findByTechniciensContainingAndDateInterventionPrevueBetween(
        Technicien technicien, LocalDateTime debut, LocalDateTime fin);

    // Compter les interventions en cours d'un technicien
    long countByTechniciensContainingAndStatut(
        Technicien technicien, StatutBonTravail statut);

    // Total des interventions assignées ce mois (peu importe le statut)
    long countByTechniciensContainingAndDateInterventionPrevueAfter(
        Technicien technicien, LocalDateTime depuis);

    // Terminées ce mois (basé sur la date de fin réelle)
    long countByTechniciensContainingAndStatutAndDateFinReelleAfter(
        Technicien technicien, StatutBonTravail statut, LocalDateTime depuis);

    Optional<BonTravail> findByDemandeMaintenanceId(Long demandeMaintenanceId);

}