package com.example.backend_siop.maintenance.repository;

import com.example.backend_siop.maintenance.entity.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    @Query("""
        SELECT DISTINCT e FROM Evenement e
        LEFT JOIN FETCH e.participants
        WHERE e.dateDebut <= :fin AND e.dateFin >= :debut
        AND (:technicienId IS NULL OR EXISTS (
            SELECT 1 FROM e.participants p WHERE p.id = :technicienId
        ))
        ORDER BY e.dateDebut ASC
        """)
    List<Evenement> findEvenementsDansPeriode(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin,
            @Param("technicienId") Long technicienId
    );
}