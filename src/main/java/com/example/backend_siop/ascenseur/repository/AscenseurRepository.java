package com.example.backend_siop.ascenseur.repository;

import com.example.backend_siop.ascenseur.entity.Ascenseur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AscenseurRepository extends JpaRepository<Ascenseur, Long> {
    boolean existsByNumeroSerie(String numeroSerie);
    List<Ascenseur> findByClientId(Long clientId);
    List<Ascenseur> findBySiteEntityId(Long siteId);
}