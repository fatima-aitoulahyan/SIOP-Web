package com.example.backend_siop.common.repository;

import com.example.backend_siop.common.entity.PieceJointe;
import com.example.backend_siop.common.enums.TypeEntiteJointe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PieceJointeRepository extends JpaRepository<PieceJointe, Long> {

    List<PieceJointe> findByEntiteTypeAndEntiteId(TypeEntiteJointe entiteType, Long entiteId);

    void deleteByEntiteTypeAndEntiteId(TypeEntiteJointe entiteType, Long entiteId);

    long countByEntiteTypeAndEntiteId(TypeEntiteJointe entiteType, Long entiteId);
}