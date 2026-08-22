package com.example.backend_siop.utilisateur.service;

import com.example.backend_siop.utilisateur.dto.*;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UtilisateurService {
    UtilisateurResponseDTO creerUtilisateur(UtilisateurRequestDTO dto);
    UtilisateurResponseDTO getById(Long id);
    List<UtilisateurResponseDTO> getAll();
    UtilisateurResponseDTO modifier(Long id, UtilisateurRequestDTO dto);
    void supprimer(Long id);
    void desactiver(Long id);
    void activerCompte(ActivationCompteDTO dto);
    List<UtilisateurResponseDTO> getClients();
    void demanderReinitialisationMotDePasse(MotDePasseOublieRequestDTO dto);
    void reinitialiserMotDePasse(ReinitialisationMotDePasseDTO dto);
    ProfilDTO getProfil(Utilisateur utilisateur);
    ProfilDTO modifierMonProfil(Utilisateur utilisateur, ModifierProfilDTO dto);
    ProfilDTO modifierPhotoProfil(Utilisateur utilisateur, MultipartFile fichier);
    org.springframework.core.io.Resource getPhotoProfil(Utilisateur utilisateur);
}