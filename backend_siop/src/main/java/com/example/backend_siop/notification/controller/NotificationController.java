package com.example.backend_siop.notification.controller;

import com.example.backend_siop.notification.dto.NotificationDTO;
import com.example.backend_siop.notification.service.NotificationService;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDTO> mesNotifications(@AuthenticationPrincipal Utilisateur user) {
        return notificationService.lister(user.getId());
    }

    @GetMapping("/non-lues/count")
    public long compterNonLues(@AuthenticationPrincipal Utilisateur user) {
        return notificationService.compterNonLues(user.getId());
    }

    @PatchMapping("/{id}/lire")
    public void marquerLue(@PathVariable Long id, @AuthenticationPrincipal Utilisateur user) {
        notificationService.marquerCommeLue(id, user.getId());
    }
}