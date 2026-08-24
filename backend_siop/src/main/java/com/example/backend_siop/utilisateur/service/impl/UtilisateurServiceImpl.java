package com.example.backend_siop.utilisateur.service.impl;

import com.example.backend_siop.common.exception.BusinessRuleException;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.common.util.FileStorageUtil;
import com.example.backend_siop.parc.entity.Parc;
import com.example.backend_siop.parc.repository.ParcRepository;
import com.example.backend_siop.utilisateur.dto.ActivationCompteDTO;
import com.example.backend_siop.utilisateur.dto.ModifierProfilDTO;
import com.example.backend_siop.utilisateur.dto.MotDePasseOublieRequestDTO;
import com.example.backend_siop.utilisateur.dto.ProfilDTO;
import com.example.backend_siop.utilisateur.dto.ReinitialisationMotDePasseDTO;
import com.example.backend_siop.utilisateur.dto.UtilisateurRequestDTO;
import com.example.backend_siop.utilisateur.dto.UtilisateurResponseDTO;
import com.example.backend_siop.utilisateur.dto.mapper.UtilisateurMapper;
import com.example.backend_siop.utilisateur.entity.*;
import com.example.backend_siop.utilisateur.repository.UtilisateurRepository;
import com.example.backend_siop.utilisateur.service.EmailService;
import com.example.backend_siop.utilisateur.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final FileStorageUtil fileStorageUtil;
    private final ParcRepository parcRepository;

    @Override
    public UtilisateurResponseDTO creerUtilisateur(UtilisateurRequestDTO dto) {
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessRuleException("Un utilisateur avec cet email existe déjà.");
        }

        if (dto.getMotDePasse() == null || dto.getMotDePasse().trim().isEmpty()) {
            throw new BusinessRuleException("Le mot de passe est obligatoire lors de la création.");
        }

        if (dto.getMotDePasse().length() < 6) {
            throw new BusinessRuleException("Le mot de passe doit contenir au moins 6 caractères.");
        }

        System.out.println(" DEBUG - Type d'utilisateur: " + dto.getType());
        System.out.println(" DEBUG - parcIds reçus du frontend: " + dto.getParcIds());

        Utilisateur utilisateur = switch (dto.getType()) {
            case ADMINISTRATEUR -> new Administrateur();
            case RESPONSABLE_MAINTENANCE -> new ResponsableMaintenance();
            case CLIENT -> {
                Client c = new Client();
                c.setAdresse(dto.getAdresse());
                yield c;
            }
            case TECHNICIEN -> {
                Technicien t = new Technicien();
                t.setSpecialite(dto.getSpecialite());
                yield t;
            }
        };

        appliquerChampsCommuns(utilisateur, dto);

        if (utilisateur instanceof Technicien technicien && dto.getParcIds() != null && !dto.getParcIds().isEmpty()) {
            List<Parc> parcsAssignes = parcRepository.findAllById(dto.getParcIds());
            System.out.println(" DEBUG - Parcs trouvés en BDD pour ces IDs: " + parcsAssignes.size());
            technicien.setParcs(parcsAssignes);
        } else {
            System.out.println(" DEBUG - Aucun parc à assigner (liste vide, null, ou ce n'est pas un technicien)");
        }

        utilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        utilisateur.setActivationToken(UUID.randomUUID().toString());
        utilisateur.setActivationTokenExpiration(LocalDateTime.now().plusHours(48));

        Utilisateur saved = utilisateurRepository.save(utilisateur);

        try {
            emailService.envoyerEmailActivation(saved.getEmail(), saved.getPrenom(), saved.getActivationToken());
        } catch (Exception e) {
            System.err.println("⚠ Email d'activation non envoyé : " + e.getMessage());
        }

        return utilisateurMapper.toDTO(saved);
    }

    @Override
    public void activerCompte(ActivationCompteDTO dto) {
        Utilisateur utilisateur = utilisateurRepository
                .findByActivationToken(dto.getToken())
                .orElseThrow(() -> new BusinessRuleException("Token d'activation invalide."));

        if (utilisateur.getActivationTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Ce lien d'activation a expiré. Contactez l'administrateur.");
        }

        utilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        utilisateur.setActif(true);
        utilisateur.setActivationToken(null);
        utilisateur.setActivationTokenExpiration(null);

        utilisateurRepository.save(utilisateur);
    }

    @Override
    public void demanderReinitialisationMotDePasse(MotDePasseOublieRequestDTO dto) {
        utilisateurRepository.findByEmail(dto.getEmail()).ifPresent(utilisateur -> {
            utilisateur.setResetPasswordToken(UUID.randomUUID().toString());
            utilisateur.setResetPasswordTokenExpiration(LocalDateTime.now().plusHours(1));
            utilisateurRepository.save(utilisateur);

            emailService.envoyerEmailReinitialisation(
                    utilisateur.getEmail(),
                    utilisateur.getPrenom(),
                    utilisateur.getResetPasswordToken()
            );
        });
        // Pas d'exception si l'email n'existe pas : on ne révèle jamais
        // si un email est enregistré ou non (protection contre l'énumération de comptes).
    }

    @Override
    public void reinitialiserMotDePasse(ReinitialisationMotDePasseDTO dto) {
        Utilisateur utilisateur = utilisateurRepository
                .findByResetPasswordToken(dto.getToken())
                .orElseThrow(() -> new BusinessRuleException("Token de réinitialisation invalide."));

        if (utilisateur.getResetPasswordTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Ce lien de réinitialisation a expiré. Veuillez refaire une demande.");
        }

        utilisateur.setMotDePasse(passwordEncoder.encode(dto.getNouveauMotDePasse()));
        utilisateur.setResetPasswordToken(null);
        utilisateur.setResetPasswordTokenExpiration(null);

        utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponseDTO getById(Long id) {
        return utilisateurMapper.toDTO(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponseDTO> getAll() {
        return utilisateurRepository.findAll().stream().map(utilisateurMapper::toDTO).toList();
    }

    @Override
    public UtilisateurResponseDTO modifier(Long id, UtilisateurRequestDTO dto) {
        Utilisateur utilisateur = findOrThrow(id);
        appliquerChampsCommuns(utilisateur, dto);

        if (utilisateur instanceof Technicien technicien && dto.getParcIds() != null) {
            List<Parc> parcsAssignes = parcRepository.findAllById(dto.getParcIds());
            technicien.setParcs(parcsAssignes);
        }

        if (dto.getMotDePasse() != null && !dto.getMotDePasse().trim().isEmpty()) {
            if (dto.getMotDePasse().length() < 6) {
                throw new BusinessRuleException("Le mot de passe doit contenir au moins 6 caractères.");
            }
            utilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        }

        utilisateur.setActif(dto.getActif() != null ? dto.getActif() : true);

        if (utilisateur instanceof Client client) {
            client.setAdresse(dto.getAdresse());
        } else if (utilisateur instanceof Technicien technicien) {
            technicien.setSpecialite(dto.getSpecialite());
        }

        return utilisateurMapper.toDTO(utilisateurRepository.save(utilisateur));
    }

    @Override
    @Transactional(readOnly = true)
    public ProfilDTO getProfil(Utilisateur utilisateur) {
        return toProfilDTO(utilisateur);
    }

    @Override
    public ProfilDTO modifierMonProfil(Utilisateur utilisateur, ModifierProfilDTO dto) {
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setTelephone(dto.getTelephone());
        utilisateur.setNomEntreprise(dto.getNomEntreprise());

        if (utilisateur instanceof Client client) {
            client.setAdresse(dto.getAdresse());
        } else if (utilisateur instanceof Technicien technicien) {
            if (dto.getSpecialite() != null) {
                technicien.setSpecialite(dto.getSpecialite());
            }
        }

        return toProfilDTO(utilisateurRepository.save(utilisateur));
    }

    @Override
    public ProfilDTO modifierPhotoProfil(Utilisateur utilisateur, MultipartFile fichier) {
        validerImage(fichier);

        if (utilisateur.getPhotoUrl() != null) {
            fileStorageUtil.delete(utilisateur.getPhotoUrl());
        }

        String cheminObjet = fileStorageUtil.store(fichier, "photos-profil");
        utilisateur.setPhotoUrl(cheminObjet);

        return toProfilDTO(utilisateurRepository.save(utilisateur));
    }

    private void validerImage(MultipartFile fichier) {
        if (fichier == null || fichier.isEmpty()) {
            throw new BusinessRuleException("Aucun fichier fourni.");
        }

        if (fichier.getContentType() == null || !fichier.getContentType().startsWith("image/")) {
            throw new BusinessRuleException("Le fichier doit être une image.");
        }

        if (fichier.getSize() > 5 * 1024 * 1024) {
            throw new BusinessRuleException("L'image ne doit pas dépasser 5 Mo.");
        }
    }

    private ProfilDTO toProfilDTO(Utilisateur utilisateur) {
        ProfilDTO dto = new ProfilDTO();
        dto.setId(utilisateur.getId());
        dto.setEmail(utilisateur.getEmail());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setRole(utilisateur.getType().name());
        dto.setTelephone(utilisateur.getTelephone());
        dto.setNomEntreprise(utilisateur.getNomEntreprise());
        dto.setActif(utilisateur.isActif());
        dto.setCreatedAt(utilisateur.getCreatedAt());

        if (utilisateur instanceof Client client) {
            dto.setAdresse(client.getAdresse());
        } else if (utilisateur instanceof Technicien technicien) {
            dto.setSpecialite(technicien.getSpecialite());
        }

        if (utilisateur.getPhotoUrl() != null) {
            dto.setPhotoUrl(fileStorageUtil.getUrlTemporaire(utilisateur.getPhotoUrl()));
        }

        return dto;
    }

    @Override
    public void supprimer(Long id) {
        Utilisateur utilisateur = findOrThrow(id);
        try {
            utilisateurRepository.delete(utilisateur);
            utilisateurRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException(
                "Impossible de supprimer cet utilisateur : il est lié à des données existantes " +
                "(bons de travail, ascenseurs, etc.). Désactivez-le plutôt que de le supprimer."
            );
        }
    }

    @Override
    public void desactiver(Long id) {
        Utilisateur utilisateur = findOrThrow(id);
        utilisateur.setActif(false);
        utilisateurRepository.save(utilisateur);
    }

    private Utilisateur findOrThrow(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + id));
    }

    private void appliquerChampsCommuns(Utilisateur utilisateur, UtilisateurRequestDTO dto) {
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setTelephone(dto.getTelephone());
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setNomEntreprise(dto.getNomEntreprise());
    }

    @Override
    public List<UtilisateurResponseDTO> getClients() {
        return utilisateurRepository.findAll()
                .stream()
                .filter(u -> u instanceof Client)
                .map(utilisateurMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.core.io.Resource getPhotoProfil(Utilisateur utilisateur) {
        if (utilisateur.getPhotoUrl() == null || utilisateur.getPhotoUrl().isEmpty()) {
            return null;
        }

        try {
            java.io.InputStream stream = fileStorageUtil.download(utilisateur.getPhotoUrl());
            return new org.springframework.core.io.InputStreamResource(stream);
        } catch (Exception e) {
            System.err.println("Erreur lecture photo profil: " + e.getMessage());
            return null;
        }
    }
}