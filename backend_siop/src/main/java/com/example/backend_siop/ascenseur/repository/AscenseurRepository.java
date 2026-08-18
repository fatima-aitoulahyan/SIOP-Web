package com.example.backend_siop.ascenseur.repository;

import com.example.backend_siop.ascenseur.entity.Ascenseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AscenseurRepository extends JpaRepository<Ascenseur, Long> {
    boolean existsByNumeroSerie(String numeroSerie);
    List<Ascenseur> findByClientId(Long clientId);
    List<Ascenseur> findBySiteEntityId(Long siteId);

    //  Recherche floue multi‑critères (utilisée par l'API d'intégration)
    @Query("""
        SELECT DISTINCT a FROM Ascenseur a
        LEFT JOIN FETCH a.siteEntity s
        LEFT JOIN FETCH s.ville v
        LEFT JOIN FETCH s.client c
        WHERE
            LOWER(a.nom) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(a.numeroSerie) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(a.codeBarre) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(s.adresse) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(v.nom) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(c.nom) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(c.nomEntreprise) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY a.nom ASC
        """)
    List<Ascenseur> rechercherParTexteLibre(@Param("query") String query);

}