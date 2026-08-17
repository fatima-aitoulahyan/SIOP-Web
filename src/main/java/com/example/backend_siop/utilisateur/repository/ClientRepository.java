package com.example.backend_siop.utilisateur.repository;

import com.example.backend_siop.utilisateur.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {}