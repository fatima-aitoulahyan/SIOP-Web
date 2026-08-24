package com.example.backend_siop.maintenance.repository;

import com.example.backend_siop.maintenance.entity.ItemCheckList;
import com.example.backend_siop.maintenance.enums.GraviteAnomalie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemCheckListRepository extends JpaRepository<ItemCheckList, Long> {

    List<ItemCheckList> findByChecklistMaintenanceIdOrderByOrdreAsc(Long checklistMaintenanceId);

    @Query("""
            SELECT i FROM ItemCheckList i
            WHERE i.gravite = :gravite
              AND i.checklistMaintenance.heureDepart IS NOT NULL
              AND i.checklistMaintenance.updatedAt >= :depuis
            ORDER BY i.checklistMaintenance.updatedAt DESC
            """)
    List<ItemCheckList> findAnomaliesCritiquesRecentes(
            @Param("gravite") GraviteAnomalie gravite,
            @Param("depuis") LocalDateTime depuis);
}