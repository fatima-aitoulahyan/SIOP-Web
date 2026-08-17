package com.example.backend_siop.maintenance.service.impl;

import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.maintenance.dto.EvenementRequestDTO;
import com.example.backend_siop.maintenance.entity.Evenement;
import com.example.backend_siop.maintenance.repository.EvenementRepository;
import com.example.backend_siop.maintenance.service.EvenementService;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import com.example.backend_siop.utilisateur.repository.TechnicienRepository;
import com.example.backend_siop.utilisateur.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvenementServiceImpl implements EvenementService {

    private final EvenementRepository evenementRepository;
    private final TechnicienRepository technicienRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional
    public Evenement creerEvenement(EvenementRequestDTO dto, Long creePar) {
        Evenement evenement = new Evenement();
        evenement.setTitre(dto.getTitre());
        evenement.setDescription(dto.getDescription());
        evenement.setType(dto.getType());
        evenement.setDateDebut(dto.getDateDebut());
        evenement.setDateFin(dto.getDateFin());
        evenement.setLieu(dto.getLieu());

        Utilisateur createur = utilisateurRepository.findById(creePar)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable: " + creePar));
        evenement.setCreePar(createur);

        if (dto.getTechnicienIds() != null && !dto.getTechnicienIds().isEmpty()) {
            List<Technicien> participants =
                    technicienRepository.findAllById(dto.getTechnicienIds());
            evenement.setParticipants(participants);
        }

        return evenementRepository.save(evenement);
    }

    @Override
    @Transactional
    public Evenement modifierEvenement(Long id, EvenementRequestDTO dto) {
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evenement introuvable: " + id));

        evenement.setTitre(dto.getTitre());
        evenement.setDescription(dto.getDescription());
        evenement.setType(dto.getType());
        evenement.setDateDebut(dto.getDateDebut());
        evenement.setDateFin(dto.getDateFin());
        evenement.setLieu(dto.getLieu());

        if (dto.getTechnicienIds() != null) {
            List<Technicien> participants =
                    technicienRepository.findAllById(dto.getTechnicienIds());
            evenement.setParticipants(participants);
        }

        return evenementRepository.save(evenement);
    }

    @Override
    @Transactional
    public void supprimerEvenement(Long id) {
        if (!evenementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evenement introuvable: " + id);
        }
        evenementRepository.deleteById(id);
    }
}