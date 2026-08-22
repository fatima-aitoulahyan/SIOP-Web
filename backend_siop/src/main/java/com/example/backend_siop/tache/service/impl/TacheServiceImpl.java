package com.example.backend_siop.tache.service.impl;

import com.example.backend_siop.ascenseur.entity.Ascenseur;
import com.example.backend_siop.ascenseur.repository.AscenseurRepository;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.tache.dto.TacheCreateDTO;
import com.example.backend_siop.tache.dto.TacheDTO;
import com.example.backend_siop.tache.dto.TacheUpdateStatutDTO;
import com.example.backend_siop.tache.entity.Tache;
import com.example.backend_siop.tache.enums.StatutTache;
import com.example.backend_siop.tache.repository.TacheRepository;
import com.example.backend_siop.tache.service.TacheService;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import com.example.backend_siop.utilisateur.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TacheServiceImpl implements TacheService {

    private final TacheRepository tacheRepository;
    private final AscenseurRepository ascenseurRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    public TacheDTO creer(TacheCreateDTO dto, Long createurId) {
        Ascenseur ascenseur = ascenseurRepository.findById(dto.getAscenseurId())
                .orElseThrow(() -> new ResourceNotFoundException("Ascenseur introuvable"));

        Utilisateur responsable = utilisateurRepository.findById(dto.getResponsableId())
                .orElseThrow(() -> new ResourceNotFoundException("Responsable introuvable"));

        Utilisateur createur = utilisateurRepository.findById(createurId)
                .orElseThrow(() -> new ResourceNotFoundException("Créateur introuvable"));

        Tache tache = new Tache();
        tache.setTitre(dto.getTitre());
        tache.setDescription(dto.getDescription());
        tache.setType(dto.getType());
        tache.setPriorite(dto.getPriorite());
        tache.setDateEcheance(dto.getDateEcheance());
        tache.setAscenseur(ascenseur);
        tache.setResponsable(responsable);
        tache.setCreateur(createur);
        tache.setStatut(StatutTache.A_FAIRE);
        tache.setTechniciens(new ArrayList<>());

        Tache saved = tacheRepository.save(tache);
        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TacheDTO getById(Long id) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));
        return toDTO(tache);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TacheDTO> listerToutes() {
        return tacheRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TacheDTO> listerParResponsable(Long responsableId) {
        return tacheRepository.findByResponsableId(responsableId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TacheDTO> listerParTechnicien(Long technicienId) {
        return tacheRepository.findByTechniciensId(technicienId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public TacheDTO modifierStatut(Long id, TacheUpdateStatutDTO dto) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));

        tache.setStatut(dto.getStatut());
        if (dto.getStatut() == StatutTache.TERMINE) {
            tache.setDateCompletion(LocalDateTime.now());
        }

        Tache saved = tacheRepository.save(tache);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public TacheDTO assignerTechniciens(Long tacheId, List<Long> technicienIds, Long responsableId) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));

        if (tache.getResponsable() == null || !tache.getResponsable().getId().equals(responsableId)) {
            throw new AccessDeniedException("Cette tâche ne vous est pas assignée.");
        }

        List<Utilisateur> nouveauxTechniciens = new ArrayList<>();
        for (Long techId : technicienIds) {
            Utilisateur tech = utilisateurRepository.findById(techId)
                    .orElseThrow(() -> new ResourceNotFoundException("Technicien introuvable"));
            nouveauxTechniciens.add(tech);
        }

        tache.setTechniciens(nouveauxTechniciens);
        if (tache.getStatut() == StatutTache.A_FAIRE) {
            tache.setStatut(StatutTache.EN_COURS);
        }

        Tache saved = tacheRepository.save(tache);
        return toDTO(saved);
    }

    @Override
    public void supprimer(Long id) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));
        tacheRepository.delete(tache);
    }

    private TacheDTO toDTO(Tache t) {
        TacheDTO dto = new TacheDTO();
        dto.setId(t.getId());
        dto.setTitre(t.getTitre());
        dto.setDescription(t.getDescription());
        dto.setType(t.getType());
        dto.setStatut(t.getStatut());
        dto.setPriorite(t.getPriorite());
        dto.setDateEcheance(t.getDateEcheance());
        dto.setDateCreation(t.getDateCreation());
        dto.setDateCompletion(t.getDateCompletion());

        if (t.getAscenseur() != null) {
            dto.setAscenseurId(t.getAscenseur().getId());
            dto.setAscenseurNom(t.getAscenseur().getNom());
            if (t.getAscenseur().getSiteEntity() != null) {
                dto.setAscenseurSite(t.getAscenseur().getSiteEntity().getAdresse());
            }
            if (t.getAscenseur().getClient() != null) {
                dto.setAscenseurClient(t.getAscenseur().getClient().getNomEntreprise());
            }
        }

        if (t.getCreateur() != null) {
            dto.setCreateurId(t.getCreateur().getId());
            dto.setCreateurNom(t.getCreateur().getPrenom() + " " + t.getCreateur().getNom());
        }

        if (t.getResponsable() != null) {
            dto.setResponsableId(t.getResponsable().getId());
            dto.setResponsableNom(t.getResponsable().getPrenom() + " " + t.getResponsable().getNom());
        }

        if (t.getTechniciens() != null && !t.getTechniciens().isEmpty()) {
            List<Long> ids = new ArrayList<>();
            List<String> noms = new ArrayList<>();
            for (Utilisateur tech : t.getTechniciens()) {
                ids.add(tech.getId());
                noms.add(tech.getPrenom() + " " + tech.getNom());
            }
            dto.setTechnicienIds(ids);
            dto.setTechnicienNoms(noms);
        } else {
            dto.setTechnicienIds(new ArrayList<>());
            dto.setTechnicienNoms(new ArrayList<>());
        }

        return dto;
    }
}