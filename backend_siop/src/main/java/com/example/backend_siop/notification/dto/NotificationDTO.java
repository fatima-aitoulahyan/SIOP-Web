package com.example.backend_siop.notification.dto;

import com.example.backend_siop.notification.enums.TypeNotification;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private Long id;
    private TypeNotification type;
    private String titre;
    private String message;
    private String entiteType;
    private Long entiteId;
    private boolean lu;
    private LocalDateTime dateCreation;
}