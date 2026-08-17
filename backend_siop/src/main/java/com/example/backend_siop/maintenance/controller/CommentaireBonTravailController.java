package com.example.backend_siop.maintenance.controller;

import com.example.backend_siop.maintenance.dto.CommentaireDTO;
import com.example.backend_siop.maintenance.dto.NouveauCommentaireDTO;
import com.example.backend_siop.maintenance.service.impl.CommentaireBonTravailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bons-travail/{btId}/commentaires")
@RequiredArgsConstructor
public class CommentaireBonTravailController {

    private final CommentaireBonTravailService service;

    @GetMapping
    public List<CommentaireDTO> lister(@PathVariable Long btId) {
        return service.listerParBonTravail(btId);
    }

    @PostMapping
    public CommentaireDTO ajouter(
            @PathVariable Long btId,
            @RequestBody @Valid NouveauCommentaireDTO dto,
            Authentication auth
    ) {
        return service.ajouter(btId, dto.contenu(), auth.getName());
    }
    @DeleteMapping("/{commentaireId}")
    public void supprimer(
            @PathVariable Long btId,
            @PathVariable Long commentaireId,
            Authentication auth
    ) {
        service.supprimer(btId, commentaireId, auth.getName());
    }
}