package com.example.backend_siop.notification.dto.mapper;

import com.example.backend_siop.notification.dto.NotificationDTO;
import com.example.backend_siop.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDTO toDTO(Notification n) {
        return new NotificationDTO(
                n.getId(),
                n.getType(),
                n.getTitre(),
                n.getMessage(),
                n.getEntiteType(),
                n.getEntiteId(),
                n.isLu(),
                n.getDateCreation()
        );
    }
}