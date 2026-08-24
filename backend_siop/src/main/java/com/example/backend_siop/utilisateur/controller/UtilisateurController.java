package com.example.backend_siop.utilisateur.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.utilisateur.dto.ModifierProfilDTO;
import com.example.backend_siop.utilisateur.dto.ProfilDTO;
import com.example.backend_siop.utilisateur.dto.UtilisateurRequestDTO;
import com.example.backend_siop.utilisateur.dto.UtilisateurResponseDTO;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import com.example.backend_siop.utilisateur.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    // ─── PROFIL UTILISATEUR CONNECTÉ ─────────────────────────────

    @GetMapping("/profil")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ProfilDTO> getProfil(@AuthenticationPrincipal Utilisateur user) {
        return ApiResponse.success(utilisateurService.getProfil(user));
    }

    @GetMapping("/photo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> getMaPhoto(
            @AuthenticationPrincipal Utilisateur user) {
        try {
            Resource resource = utilisateurService.getPhotoProfil(user);
            if (resource == null || !resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Déterminer le Content-Type selon l'extension
            String contentType = "image/jpeg";
            String photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                String ext = photoUrl.toLowerCase();
                if (ext.endsWith(".png")) contentType = "image/png";
                else if (ext.endsWith(".webp")) contentType = "image/webp";
                else if (ext.endsWith(".gif")) contentType = "image/gif";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/profil")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ProfilDTO> modifierMonProfil(
            @Valid @RequestBody ModifierProfilDTO dto,
            @AuthenticationPrincipal Utilisateur user) {
        return ApiResponse.success(utilisateurService.modifierMonProfil(user, dto));
    }

    @PutMapping("/profil/photo")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ProfilDTO> modifierMaPhoto(
            @RequestParam("photo") MultipartFile photo,
            @AuthenticationPrincipal Utilisateur user) {
        return ApiResponse.success(utilisateurService.modifierPhotoProfil(user, photo));
    }

    // ─── GESTION ADMIN DES UTILISATEURS ────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR')")
    public ApiResponse<UtilisateurResponseDTO> creer(@Valid @RequestBody UtilisateurRequestDTO dto) {
        return ApiResponse.success(utilisateurService.creerUtilisateur(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE', 'ROLE_RESPONSABLE_MAINTENANCE')")
    public ApiResponse<UtilisateurResponseDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(utilisateurService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE', 'ROLE_RESPONSABLE_MAINTENANCE')")
    public ApiResponse<List<UtilisateurResponseDTO>> getAll() {
        return ApiResponse.success(utilisateurService.getAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR')")
    public ApiResponse<UtilisateurResponseDTO> modifier(@PathVariable Long id, @Valid @RequestBody UtilisateurRequestDTO dto) {
        return ApiResponse.success(utilisateurService.modifier(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR')")
    public ApiResponse<Void> supprimer(@PathVariable Long id) {
        utilisateurService.supprimer(id);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{id}/desactiver")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR')")
    public ApiResponse<Void> desactiver(@PathVariable Long id) {
        utilisateurService.desactiver(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/clients")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATEUR', 'ROLE_ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE', 'ROLE_RESPONSABLE_MAINTENANCE', 'CLIENT', 'ROLE_CLIENT')")
    public ResponseEntity<ApiResponse<List<UtilisateurResponseDTO>>> getClients() {
        return ResponseEntity.ok(
                ApiResponse.success(utilisateurService.getClients())
        );
    }
}