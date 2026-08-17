package com.example.backend_siop.notification.service.impl;

import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.notification.dto.NotificationDTO;
import com.example.backend_siop.notification.entity.Notification;
import com.example.backend_siop.notification.enums.TypeNotification;
import com.example.backend_siop.notification.repository.NotificationRepository;
import com.example.backend_siop.notification.service.NotificationService;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void creer(Utilisateur destinataire, TypeNotification type,
                      String titre, String message, String entiteType, Long entiteId) {
        Notification n = new Notification();
        n.setDestinataire(destinataire);
        n.setType(type);
        n.setTitre(titre);
        n.setMessage(message);
        n.setEntiteType(entiteType);
        n.setEntiteId(entiteId);
        notificationRepository.save(n);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> lister(Long utilisateurId) {
        return notificationRepository.findByDestinataireIdOrderByDateCreationDesc(utilisateurId)
                .stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long compterNonLues(Long utilisateurId) {
        return notificationRepository.countByDestinataireIdAndLuFalse(utilisateurId);
    }

    @Override
    @Transactional
    public void marquerCommeLue(Long notificationId, Long utilisateurId) {
        Notification n = notificationRepository.findByIdAndDestinataireId(notificationId, utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification introuvable"));
        n.setLu(true);
        notificationRepository.save(n);
    }

    private NotificationDTO toDTO(Notification n) {
        return new NotificationDTO(n.getId(), n.getType(), n.getTitre(), n.getMessage(),
                n.getEntiteType(), n.getEntiteId(), n.isLu(), n.getDateCreation());
    }
}