package com.example.backend_siop.referentiel.service;

import com.example.backend_siop.common.exception.BusinessRuleException;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.referentiel.dto.VilleCreateDTO;
import com.example.backend_siop.referentiel.dto.VilleResponseDTO;
import com.example.backend_siop.referentiel.entity.Ville;
import com.example.backend_siop.referentiel.repository.VilleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VilleService {

    private final VilleRepository villeRepository;

    @Transactional(readOnly = true)
    public List<VilleResponseDTO> listerToutes() {
        return villeRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public VilleResponseDTO creer(VilleCreateDTO dto) {
        if (villeRepository.existsByNom(dto.getNom())) {
            throw new BusinessRuleException("Une ville portant ce nom existe déjà.");
        }
        Ville ville = new Ville();
        ville.setNom(dto.getNom());
        ville.setRegion(dto.getRegion());
        ville.setCodePostal(dto.getCodePostal());
        return toDto(villeRepository.save(ville));
    }

    @Transactional
    public VilleResponseDTO modifier(Long id, VilleCreateDTO dto) {
        Ville ville = villeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable"));

        if (!ville.getNom().equals(dto.getNom())
                && villeRepository.existsByNom(dto.getNom())) {
            throw new BusinessRuleException("Une ville portant ce nom existe déjà.");
        }

        ville.setNom(dto.getNom());
        ville.setRegion(dto.getRegion());
        ville.setCodePostal(dto.getCodePostal());
        return toDto(villeRepository.save(ville));
    }

    private VilleResponseDTO toDto(Ville ville) {
        VilleResponseDTO dto = new VilleResponseDTO();
        dto.setId(ville.getId());
        dto.setNom(ville.getNom());
        dto.setRegion(ville.getRegion());
        dto.setCodePostal(ville.getCodePostal());
        dto.setActive(ville.isActive());
        return dto;
    }
}