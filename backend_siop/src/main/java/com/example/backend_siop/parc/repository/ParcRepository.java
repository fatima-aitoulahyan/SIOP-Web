package com.example.backend_siop.parc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend_siop.parc.entity.Parc;
public interface ParcRepository extends JpaRepository<Parc, Long> {
    List<Parc> findAllByOrderByNomAsc();
    Optional<Parc> findByNom(String nom);
    boolean existsByNom(String nom);
}