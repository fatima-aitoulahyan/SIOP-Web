package com.example.backend_siop.utilisateur.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.security.jwt.JwtTokenProvider;
import com.example.backend_siop.utilisateur.dto.ActivationCompteDTO;
import com.example.backend_siop.utilisateur.dto.LoginRequest;
import com.example.backend_siop.utilisateur.dto.LoginResponse;
import com.example.backend_siop.utilisateur.dto.ModifierProfilDTO;
import com.example.backend_siop.utilisateur.dto.ProfilDTO;
import com.example.backend_siop.utilisateur.dto.MotDePasseOublieRequestDTO;
import com.example.backend_siop.utilisateur.dto.ReinitialisationMotDePasseDTO;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import com.example.backend_siop.utilisateur.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UtilisateurService utilisateurService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse()));

        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(authentication);

        LoginResponse response = LoginResponse.builder()
                .id(utilisateur.getId())
                .token(token)
                .email(utilisateur.getEmail())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .type(utilisateur.getType().name())
                .build();

        return ApiResponse.success(response);
    }
    @PostMapping("/activer-compte")
    public ApiResponse<Void> activerCompte(@Valid @RequestBody ActivationCompteDTO dto) {
        utilisateurService.activerCompte(dto);
        return ApiResponse.success(null);
    }

    @PostMapping("/mot-de-passe-oublie")
    public ApiResponse<Void> motDePasseOublie(@Valid @RequestBody MotDePasseOublieRequestDTO dto) {
        utilisateurService.demanderReinitialisationMotDePasse(dto);
        return ApiResponse.success(null);
    }

    @PostMapping("/reinitialiser-mot-de-passe")
    public ApiResponse<Void> reinitialiserMotDePasse(@Valid @RequestBody ReinitialisationMotDePasseDTO dto) {
        utilisateurService.reinitialiserMotDePasse(dto);
        return ApiResponse.success(null);
    }

    @GetMapping("/me")
    public ApiResponse<ProfilDTO> me(@AuthenticationPrincipal Utilisateur utilisateur) {
        return ApiResponse.success(utilisateurService.getProfil(utilisateur));
    }

    @PutMapping("/mon-profil")
    public ApiResponse<ProfilDTO> modifierMonProfil(
            @AuthenticationPrincipal Utilisateur utilisateur,
            @Valid @RequestBody ModifierProfilDTO dto) {
        return ApiResponse.success(utilisateurService.modifierMonProfil(utilisateur, dto));
    }

    @PostMapping("/photo-de-profil")
    public ApiResponse<ProfilDTO> modifierPhotoProfil(
            @AuthenticationPrincipal Utilisateur utilisateur,
            @RequestParam("file") MultipartFile fichier) {
        return ApiResponse.success(utilisateurService.modifierPhotoProfil(utilisateur, fichier));
    }
}