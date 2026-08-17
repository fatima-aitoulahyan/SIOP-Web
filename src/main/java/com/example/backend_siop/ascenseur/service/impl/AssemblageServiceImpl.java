package com.example.backend_siop.ascenseur.service.impl;

import com.example.backend_siop.ascenseur.dto.Assemblage.AssemblageCreateDTO;
import com.example.backend_siop.ascenseur.dto.Assemblage.AssemblageDTO;
import com.example.backend_siop.ascenseur.dto.Assemblage.AssemblageTreeDTO;
import com.example.backend_siop.ascenseur.dto.Assemblage.AssemblageUpdateDTO;
import com.example.backend_siop.ascenseur.dto.Assemblage.mapper.AssemblageMapper;
import com.example.backend_siop.ascenseur.dto.composant.mapper.ComposantMapper;
import com.example.backend_siop.ascenseur.entity.Ascenseur;
import com.example.backend_siop.ascenseur.entity.Assemblage;
import com.example.backend_siop.ascenseur.repository.AscenseurRepository;
import com.example.backend_siop.ascenseur.repository.AssemblageRepository;
import com.example.backend_siop.ascenseur.service.AssemblageService;
import com.example.backend_siop.common.exception.BusinessRuleException;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.common.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssemblageServiceImpl implements AssemblageService {

    private final AssemblageRepository assemblageRepository;
    private final AscenseurRepository ascenseurRepository;
    private final AssemblageMapper mapper;
    private final ComposantMapper composantMapper;
    private final FileStorageUtil fileStorageUtil;

    @Override
    @Transactional
    public AssemblageDTO creer(AssemblageCreateDTO dto) {
        Ascenseur ascenseur = ascenseurRepository.findById(dto.getAscenseurId())
                .orElseThrow(() -> new ResourceNotFoundException("Ascenseur introuvable"));

        Assemblage assemblage = new Assemblage();
        assemblage.setNom(dto.getNom());
        assemblage.setReference(dto.getReference());
        assemblage.setType(dto.getType());
        assemblage.setFabricant(dto.getFabricant());
        assemblage.setDateInstallation(dto.getDateInstallation());
        assemblage.setDureeVieEstimeeMois(dto.getDureeVieEstimeeMois());
        assemblage.setDescription(dto.getDescription());
        assemblage.setImageUrl(dto.getImageUrl());
        assemblage.setAscenseur(ascenseur);

        int niveau = 0;
        if (dto.getParentId() != null) {
            Assemblage parent = resolveParent(dto.getParentId(), ascenseur, null);
            assemblage.setParent(parent);
            niveau = parent.getNiveau() + 1;
        }
        assemblage.setNiveau(niveau);

        return mapper.toDTO(assemblageRepository.save(assemblage));
    }
    @Override
    @Transactional(readOnly = true)
    public AssemblageDTO getById(Long id) {
        return mapper.toDTO(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssemblageDTO> listerTous() {
        return mapper.toDTOList(assemblageRepository.findAll());
    }

    @Override
    @Transactional
    public AssemblageDTO modifier(Long id, AssemblageUpdateDTO dto) {
        Assemblage assemblage = findOrThrow(id);

        assemblage.setNom(dto.getNom());
        assemblage.setReference(dto.getReference());
        assemblage.setType(dto.getType());
        assemblage.setFabricant(dto.getFabricant());
        assemblage.setDateInstallation(dto.getDateInstallation());
        assemblage.setDureeVieEstimeeMois(dto.getDureeVieEstimeeMois());
        assemblage.setDescription(dto.getDescription());
        assemblage.setImageUrl(dto.getImageUrl());

        if (dto.getParentId() != null) {
            if (dto.getParentId().equals(id)) {
                throw new BusinessRuleException("Un assemblage ne peut pas être son propre parent.");
            }
            Assemblage parent = resolveParent(dto.getParentId(), assemblage.getAscenseur(), assemblage);
            assemblage.setParent(parent);
            assemblage.setNiveau(parent.getNiveau() + 1);   // ← recalculé aussi ici
        } else {
            assemblage.setParent(null);
            assemblage.setNiveau(0);                          // ← recalculé aussi ici
        }

        return mapper.toDTO(assemblageRepository.save(assemblage));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        Assemblage assemblage = findOrThrow(id);

        if (!assemblage.getSousAssemblages().isEmpty()) {
            throw new BusinessRuleException(
                    "Impossible de supprimer un assemblage contenant des sous-assemblages.");
        }

        if (assemblage.getImageUrl() != null) {
            fileStorageUtil.delete(assemblage.getImageUrl());
        }

        assemblageRepository.delete(assemblage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssemblageDTO> listerParParent(Long parentId) {
        return mapper.toDTOList(assemblageRepository.findByParentId(parentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssemblageDTO> listerParAscenseur(Long ascenseurId) {
        if (ascenseurId == null) return Collections.emptyList();
        return mapper.toDTOList(assemblageRepository.findByAscenseurId(ascenseurId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssemblageTreeDTO> listerArbreParAscenseur(Long ascenseurId) {
        if (ascenseurId == null) return Collections.emptyList();
        List<Assemblage> racines =
                assemblageRepository.findByAscenseurIdAndParentIsNull(ascenseurId);
        return mapper.toTreeDTOList(racines, composantMapper);
    }

    private Assemblage findOrThrow(Long id) {
        return assemblageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assemblage introuvable"));
    }

    private Assemblage resolveParent(Long parentId, Ascenseur ascenseur, Assemblage courant) {
        Assemblage parent = assemblageRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assemblage parent introuvable"));

        if (!parent.getAscenseur().getId().equals(ascenseur.getId())) {
            throw new BusinessRuleException(
                    "L'assemblage parent doit appartenir au même ascenseur.");
        }

        if (courant != null && estDescendant(courant, parent)) {
            throw new BusinessRuleException(
                    "Cycle détecté : le parent choisi est un descendant de cet assemblage.");
        }

        return parent;
    }

    private boolean estDescendant(Assemblage racine, Assemblage candidat) {
        for (Assemblage enfant : racine.getSousAssemblages()) {
            if (enfant.getId().equals(candidat.getId()) || estDescendant(enfant, candidat)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public AssemblageDTO uploaderImage(Long id, MultipartFile file) {
        Assemblage assemblage = findOrThrow(id);

        if (assemblage.getImageUrl() != null) {
            fileStorageUtil.delete(assemblage.getImageUrl());
        }

        String cheminObjet = fileStorageUtil.store(file, "assemblages");
        assemblage.setImageUrl(cheminObjet);

        return mapper.toDTO(assemblageRepository.save(assemblage));
    }
}