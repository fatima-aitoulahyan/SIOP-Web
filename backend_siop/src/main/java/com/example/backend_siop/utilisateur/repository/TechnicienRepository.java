package com.example.backend_siop.utilisateur.repository;

import com.example.backend_siop.utilisateur.entity.Technicien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TechnicienRepository extends JpaRepository<Technicien, Long> {
    List<Technicien> findByParcs_Id(Long parcId);
    long countByParcs_Id(Long parcId);

    @Query("SELECT t FROM Technicien t LEFT JOIN FETCH t.parcs WHERE t.id = :id")
    Optional<Technicien> findByIdWithParcs(@Param("id") Long id);
    List<Technicien> findByActifTrue();
}