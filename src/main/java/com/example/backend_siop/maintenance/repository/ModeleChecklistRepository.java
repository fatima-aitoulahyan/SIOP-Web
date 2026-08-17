package com.example.backend_siop.maintenance.repository;

import com.example.backend_siop.maintenance.entity.ModeleChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModeleChecklistRepository extends JpaRepository<ModeleChecklist, Long> {

    List<ModeleChecklist> findAllByOrderByOrdreAsc();
}