package com.example.backend_siop.utilisateur.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.utilisateur.dto.UtilisateurRequestDTO;
import com.example.backend_siop.utilisateur.dto.UtilisateurResponseDTO;
import com.example.backend_siop.utilisateur.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ApiResponse<UtilisateurResponseDTO> creer(@Valid @RequestBody UtilisateurRequestDTO dto) {
        return ApiResponse.success(utilisateurService.creerUtilisateur(dto));
    }

    @GetMapping("/{id}")
    public ApiResponse<UtilisateurResponseDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(utilisateurService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ApiResponse<List<UtilisateurResponseDTO>> getAll() {
        return ApiResponse.success(utilisateurService.getAll());
    }

    @PutMapping("/{id}")
    public ApiResponse<UtilisateurResponseDTO> modifier(@PathVariable Long id, @Valid @RequestBody UtilisateurRequestDTO dto) {
        return ApiResponse.success(utilisateurService.modifier(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ApiResponse<Void> supprimer(@PathVariable Long id) {
        utilisateurService.supprimer(id);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{id}/desactiver")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ApiResponse<Void> desactiver(@PathVariable Long id) {
        utilisateurService.desactiver(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/clients")
    public ResponseEntity<ApiResponse<List<UtilisateurResponseDTO>>> getClients() {
        return ResponseEntity.ok(
                ApiResponse.success(utilisateurService.getClients())
        );
    }
}