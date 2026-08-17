package com.example.backend_siop.ascenseur.repository;

import com.example.backend_siop.ascenseur.entity.Assemblage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssemblageRepository extends JpaRepository<Assemblage, Long> {

    List<Assemblage> findByAscenseurIdAndParentIsNull(Long ascenseurId);

    List<Assemblage> findByParentId(Long parentId);

    List<Assemblage> findByAscenseurId(Long ascenseurId);

    boolean existsByAscenseurId(Long ascenseurId);
}