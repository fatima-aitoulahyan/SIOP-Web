package com.example.backend_siop.ascenseur.repository;

import com.example.backend_siop.ascenseur.entity.Ascenseur;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AscenseurRepository extends JpaRepository<Ascenseur, Long> {

    boolean existsByNumeroSerie(String numeroSerie);

    @EntityGraph(attributePaths = {"client", "siteEntity"})
    List<Ascenseur> findAll();

    @EntityGraph(attributePaths = {"client", "siteEntity"})
    List<Ascenseur> findByClientId(Long clientId);

    @EntityGraph(attributePaths = {"client", "siteEntity"})
    List<Ascenseur> findBySiteEntityId(Long siteId);
}