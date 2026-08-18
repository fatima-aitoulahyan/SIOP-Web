package com.example.backend_siop.notification.repository;

import com.example.backend_siop.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataireIdOrderByDateCreationDesc(Long destinataireId);
    long countByDestinataireIdAndLuFalse(Long destinataireId);
    Optional<Notification> findByIdAndDestinataireId(Long id, Long destinataireId);
}
