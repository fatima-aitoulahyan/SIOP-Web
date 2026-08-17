package com.example.backend_siop.maintenance.repository;

import com.example.backend_siop.maintenance.entity.CommentaireBonTravail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommentaireBonTravailRepository extends JpaRepository<CommentaireBonTravail, Long> {
    List<CommentaireBonTravail> findByBonTravailIdOrderByCreatedAtAsc(Long bonTravailId);
    Optional<CommentaireBonTravail> findByIdAndBonTravailId(Long id, Long bonTravailId);
}