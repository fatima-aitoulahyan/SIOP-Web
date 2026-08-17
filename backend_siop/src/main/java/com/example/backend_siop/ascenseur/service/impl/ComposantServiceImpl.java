package com.example.backend_siop.ascenseur.service.impl;

import com.example.backend_siop.ascenseur.dto.composant.ComposantCreateDTO;
import com.example.backend_siop.ascenseur.dto.composant.ComposantDTO;
import com.example.backend_siop.ascenseur.dto.composant.ComposantUpdateDTO;
import com.example.backend_siop.ascenseur.dto.composant.mapper.ComposantMapper;
import com.example.backend_siop.ascenseur.entity.Assemblage;
import com.example.backend_siop.ascenseur.entity.Composant;
import com.example.backend_siop.ascenseur.repository.AssemblageRepository;
import com.example.backend_siop.ascenseur.repository.ComposantRepository;
import com.example.backend_siop.ascenseur.service.ComposantService;
import com.example.backend_siop.common.exception.BusinessRuleException;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.common.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComposantServiceImpl implements ComposantService {

    private final ComposantRepository composantRepository;
    private final AssemblageRepository assemblageRepository;
    private final ComposantMapper mapper;
    private final FileStorageUtil fileStorageUtil;

    @Override
    @Transactional
    public ComposantDTO creer(ComposantCreateDTO dto) {
        if (composantRepository.existsByReference(dto.getReference())) {
            throw new BusinessRuleException("Un composant avec cette référence existe déjà.");
        }

        Assemblage assemblage = assemblageRepository.findById(dto.getAssemblageId())
                .orElseThrow(() -> new ResourceNotFoundException("Assemblage introuvable"));

        Composant composant = mapper.toEntity(dto);
        composant.setAssemblage(assemblage);

        return mapper.toDTO(composantRepository.save(composant));
    }

    @Override
    @Transactional
    public ComposantDTO modifier(Long id, ComposantUpdateDTO dto) {
        Composant composant = composantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Composant introuvable"));

        if (!dto.getReference().equals(composant.getReference())
                && composantRepository.existsByReference(dto.getReference())) {
            throw new BusinessRuleException("Un composant avec cette référence existe déjà.");
        }

        mapper.updateEntityFromDto(dto, composant);

        if (dto.getAssemblageId() != null
                && !dto.getAssemblageId().equals(composant.getAssemblage().getId())) {
            Assemblage assemblage = assemblageRepository.findById(dto.getAssemblageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assemblage introuvable"));
            composant.setAssemblage(assemblage);
        }

        return mapper.toDTO(composantRepository.save(composant));
    }

    @Override
    @Transactional(readOnly = true)
    public ComposantDTO getById(Long id) {
        return mapper.toDTO(composantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Composant introuvable")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComposantDTO> listerParAssemblage(Long assemblageId) {
        return composantRepository.findByAssemblageId(assemblageId).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public ComposantDTO uploaderImage(Long id, MultipartFile file) {
        Composant composant = composantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Composant introuvable"));

        if (composant.getImageUrl() != null) {
            fileStorageUtil.delete(composant.getImageUrl());
        }

        String cheminObjet = fileStorageUtil.store(file, "composants");
        composant.setImageUrl(cheminObjet);

        return mapper.toDTO(composantRepository.save(composant));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        Composant composant = composantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Composant introuvable"));

        if (composant.getImageUrl() != null) {
            fileStorageUtil.delete(composant.getImageUrl());
        }
        composantRepository.deleteById(id);
    }
}