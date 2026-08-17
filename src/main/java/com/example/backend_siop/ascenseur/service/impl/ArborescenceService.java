package com.example.backend_siop.ascenseur.service.impl;

import com.example.backend_siop.ascenseur.entity.*;
import com.example.backend_siop.ascenseur.enums.TypeAssemblage;
import com.example.backend_siop.ascenseur.repository.AscenseurRepository;
import com.example.backend_siop.ascenseur.repository.AssemblageRepository;
import com.example.backend_siop.ascenseur.repository.AssemblageTemplateRepository;
import com.example.backend_siop.ascenseur.repository.ComposantRepository;
import com.example.backend_siop.common.exception.BusinessRuleException;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArborescenceService {

    private final AssemblageTemplateRepository templateRepo;
    private final AssemblageRepository assemblageRepo;
    private final ComposantRepository composantRepo;
    private final AscenseurRepository ascenseurRepository;
    private final AssemblageRepository assemblageRepository;
    @Transactional
    public void genererArborescence(Ascenseur ascenseur) {
        List<AssemblageTemplate> racines =
                templateRepo.findByTypeAscenseurAndParentIsNull(ascenseur.getType());

        for (AssemblageTemplate racine : racines) {
            cloner(racine, ascenseur, null);
        }
    }

    private Assemblage cloner(AssemblageTemplate template, Ascenseur ascenseur, Assemblage parentClone) {
        Assemblage clone = new Assemblage();
        clone.setNom(template.getNom());
        clone.setNiveau(template.getNiveau());
        clone.setImageUrl(template.getImageUrl());
        clone.setAscenseur(ascenseur);
        clone.setParent(parentClone);
        clone.setReference("A_DEFINIR-" + UUID.randomUUID().toString().substring(0, 8));
        clone.setType(TypeAssemblage.AUTRE); 
        assemblageRepo.save(clone);

        for (ComposantTemplate ct : template.getComposants()) {
            Composant c = new Composant();
            c.setNom(ct.getNom());
            c.setType(ct.getType());
            c.setImageUrl(ct.getImageUrl());
            c.setReference("A_DEFINIR-" + UUID.randomUUID().toString().substring(0, 8));
            c.setAssemblage(clone);
            composantRepo.save(c);
        }

        for (AssemblageTemplate sousTemplate : template.getSousAssemblages()) {
            cloner(sousTemplate, ascenseur, clone);
        }
        return clone;
    }
    @Transactional
    public void genererArborescencePourAscenseur(Long ascenseurId) {
        Ascenseur ascenseur = ascenseurRepository.findById(ascenseurId)
                .orElseThrow(() -> new ResourceNotFoundException("Ascenseur introuvable"));

        if (assemblageRepository.existsByAscenseurId(ascenseurId)) {
            throw new BusinessRuleException("Cet ascenseur a déjà une arborescence.");
        }

        genererArborescence(ascenseur);
    }
}