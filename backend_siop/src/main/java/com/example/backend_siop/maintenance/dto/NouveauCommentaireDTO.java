package com.example.backend_siop.maintenance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NouveauCommentaireDTO(
        @NotBlank @Size(max = 1000) String contenu
) {}