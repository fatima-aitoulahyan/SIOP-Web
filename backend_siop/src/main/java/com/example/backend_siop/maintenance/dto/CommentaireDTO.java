package com.example.backend_siop.maintenance.dto;

import java.time.LocalDateTime;

public record CommentaireDTO(
        Long id,
        Long auteurId,
        String auteurNom,
        String auteurRole,
        String contenu,
        LocalDateTime createdAt
) {}