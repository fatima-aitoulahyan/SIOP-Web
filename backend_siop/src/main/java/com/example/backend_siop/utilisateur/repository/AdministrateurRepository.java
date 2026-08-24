package com.example.backend_siop.utilisateur.repository;

import com.example.backend_siop.utilisateur.entity.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {
}