package com.example.backend_siop.utilisateur.repository;

import com.example.backend_siop.utilisateur.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Utilisateur> findByActivationToken(String token);
    Optional<Utilisateur> findByResetPasswordToken(String resetPasswordToken);
}