package com.example.backend_siop.referentiel.repository;

import com.example.backend_siop.referentiel.entity.Ville;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VilleRepository extends JpaRepository<Ville, Long> {
    boolean existsByNom(String nom);
}