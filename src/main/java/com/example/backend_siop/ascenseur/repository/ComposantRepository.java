package com.example.backend_siop.ascenseur.repository;

import com.example.backend_siop.ascenseur.entity.Composant;
import com.example.backend_siop.ascenseur.enums.TypeComposant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComposantRepository extends JpaRepository<Composant, Long> {

    List<Composant> findByAssemblageId(Long assemblageId);

    List<Composant> findByType(TypeComposant type);
    boolean existsByReference(String reference);
}