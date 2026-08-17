package com.example.backend_siop.ascenseur.repository;

import com.example.backend_siop.ascenseur.entity.AssemblageTemplate;
import com.example.backend_siop.ascenseur.enums.TypeAscenseur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssemblageTemplateRepository extends JpaRepository<AssemblageTemplate, Long> {

    List<AssemblageTemplate> findByTypeAscenseurAndParentIsNull(TypeAscenseur typeAscenseur);
}