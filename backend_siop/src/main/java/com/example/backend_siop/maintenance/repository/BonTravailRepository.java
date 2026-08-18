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
}