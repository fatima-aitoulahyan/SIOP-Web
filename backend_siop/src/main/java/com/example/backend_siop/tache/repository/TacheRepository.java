package com.example.backend_siop.tache.repository;

import com.example.backend_siop.tache.entity.Tache;
import com.example.backend_siop.tache.enums.StatutTache;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacheRepository extends JpaRepository<Tache, Long> {

    @EntityGraph(attributePaths = {"ascenseur", "ascenseur.client", "ascenseur.siteEntity", "createur", "responsable", "techniciens"})
    List<Tache> findByResponsableId(Long responsableId);

    @EntityGraph(attributePaths = {"ascenseur", "ascenseur.client", "ascenseur.siteEntity", "createur", "responsable", "techniciens"})
    List<Tache> findByResponsableIdAndStatut(Long responsableId, StatutTache statut);

    @EntityGraph(attributePaths = {"ascenseur", "ascenseur.client", "ascenseur.siteEntity", "responsable", "techniciens"})
    List<Tache> findAll();

    @EntityGraph(attributePaths = {"ascenseur", "ascenseur.client", "ascenseur.siteEntity", "responsable", "techniciens"})
    List<Tache> findByTechniciensId(Long technicienId);

    long countByStatut(StatutTache statut);

    boolean existsByAscenseurId(Long ascenseurId);

    long countByResponsableIdAndStatut(Long responsableId, StatutTache statut);
}