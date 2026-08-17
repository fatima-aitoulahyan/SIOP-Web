package com.example.backend_siop.notification.service;

import com.example.backend_siop.notification.dto.NotificationDTO;
import com.example.backend_siop.notification.enums.TypeNotification;
import com.example.backend_siop.utilisateur.entity.Utilisateur;

import java.util.List;

public interface NotificationService {

    void creer(Utilisateur destinataire, TypeNotification type,
               String titre, String message, String entiteType, Long entiteId);

    List<NotificationDTO> lister(Long utilisateurId);

    long compterNonLues(Long utilisateurId);

    void marquerCommeLue(Long notificationId, Long utilisateurId);
}