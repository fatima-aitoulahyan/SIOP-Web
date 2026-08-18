package com.example.backend_siop.parc.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_siop.ascenseur.dto.Site.mapper.SiteMapper;
import com.example.backend_siop.ascenseur.repository.SiteRepository;
import com.example.backend_siop.common.exception.BusinessRuleException;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.parc.dto.ParcCreateDTO;
import com.example.backend_siop.parc.dto.ParcDTO;
import com.example.backend_siop.parc.dto.ParcDetailDTO;
import com.example.backend_siop.parc.dto.mapper.ParcMapper;
import com.example.backend_siop.parc.entity.Parc;
import com.example.backend_siop.parc.repository.ParcRepository;
import com.example.backend_siop.utilisateur.dto.mapper.UtilisateurMapper;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.repository.TechnicienRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParcServiceImpl implements ParcService {

    private final ParcRepository parcRepository;
    private final SiteRepository siteRepository;
    private final TechnicienRepository technicienRepository;
    private final ParcMapper parcMapper;
    private final SiteMapper siteMapper;
    private final UtilisateurMapper utilisateurMapper;

    @Override
    @Transactional
    public ParcDTO creer(ParcCreateDTO dto) {
        if (parcRepository.existsByNom(dto.getNom())) {
            throw new BusinessRuleException("Un parc portant ce nom existe déjà.");
        }
        Parc parc = parcMapper.toEntity(dto);
        return enrichir(parcRepository.save(parc));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParcDTO> lister() {
        return parcRepository.findAllByOrderByNomAsc()
                .stream()
                .map(this::enrichir)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ParcDetailDTO getDetail(Long id) {
        Parc parc = trouverParc(id);

        ParcDetailDTO dto = new ParcDetailDTO();
        dto.setId(parc.getId());
        dto.setNom(parc.getNom());
        dto.setCreatedAt(parc.getCreatedAt());
        dto.setSites(siteRepository.findByParcId(id)
                .stream().map(siteMapper::toDTO).toList());
        dto.setTechniciens(technicienRepository.findByParcs_Id(id)
                .stream().map(utilisateurMapper::toDTO).toList());
        return dto;
    }

    @Override
    @Transactional
    public ParcDTO modifier(Long id, ParcCreateDTO dto) {
        Parc parc = trouverParc(id);

        if (!parc.getNom().equals(dto.getNom())
                && parcRepository.existsByNom(dto.getNom())) {
            throw new BusinessRuleException("Un parc portant ce nom existe déjà.");
        }
        parc.setNom(dto.getNom());
        return enrichir(parcRepository.save(parc));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        Parc parc = trouverParc(id);

        long nbSites = siteRepository.countByParcId(id);
        if (nbSites > 0) {
            throw new BusinessRuleException(
                    "Impossible de supprimer ce parc : " + nbSites
                    + " site(s) y sont rattachés. Réaffectez-les d'abord.");
        }

        technicienRepository.findByParcs_Id(id)
                .forEach(t -> t.getParcs().remove(parc));

        parcRepository.delete(parc);
    }

    @Override
    @Transactional
    public ParcDetailDTO affecterTechnicien(Long parcId, Long technicienId) {
        Parc parc = trouverParc(parcId);
        Technicien technicien = technicienRepository.findById(technicienId)
                .orElseThrow(() -> new ResourceNotFoundException("Technicien introuvable"));

        if (technicien.getParcs().contains(parc)) {
            throw new BusinessRuleException("Ce technicien couvre déjà ce parc.");
        }
        technicien.getParcs().add(parc);
        technicienRepository.save(technicien);

        return getDetail(parcId);
    }

    @Override
    @Transactional
    public ParcDetailDTO retirerTechnicien(Long parcId, Long technicienId) {
        Parc parc = trouverParc(parcId);
        Technicien technicien = technicienRepository.findById(technicienId)
                .orElseThrow(() -> new ResourceNotFoundException("Technicien introuvable"));

        if (!technicien.getParcs().remove(parc)) {
            throw new BusinessRuleException("Ce technicien ne couvre pas ce parc.");
        }
        technicienRepository.save(technicien);

        return getDetail(parcId);
    }

    private Parc trouverParc(Long id) {
        return parcRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parc introuvable"));
    }

    private ParcDTO enrichir(Parc parc) {
        ParcDTO dto = parcMapper.toDTO(parc);
        dto.setNombreSites(siteRepository.countByParcId(parc.getId()));
        dto.setNombreTechniciens(technicienRepository.countByParcs_Id(parc.getId()));
        return dto;
    }
}