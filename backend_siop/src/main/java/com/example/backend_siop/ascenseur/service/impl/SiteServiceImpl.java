package com.example.backend_siop.ascenseur.service.impl;

import com.example.backend_siop.ascenseur.dto.Site.SiteCreateDTO;
import com.example.backend_siop.ascenseur.dto.Site.SiteDTO;
import com.example.backend_siop.ascenseur.dto.Site.SiteUpdateDTO;
import com.example.backend_siop.ascenseur.dto.Site.mapper.SiteMapper;
import com.example.backend_siop.ascenseur.entity.Site;
import com.example.backend_siop.ascenseur.repository.SiteRepository;
import com.example.backend_siop.ascenseur.service.SiteService;
import com.example.backend_siop.common.exception.BusinessRuleException;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.parc.entity.Parc;
import com.example.backend_siop.parc.repository.ParcRepository;
import com.example.backend_siop.referentiel.entity.Ville;
import com.example.backend_siop.referentiel.repository.VilleRepository;
import com.example.backend_siop.utilisateur.entity.Client;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import com.example.backend_siop.utilisateur.repository.ClientRepository;
import com.example.backend_siop.utilisateur.repository.TechnicienRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;
    private final VilleRepository villeRepository;
    private final ClientRepository clientRepository;
    private final ParcRepository parcRepository;
    private final TechnicienRepository technicienRepository;
    private final SiteMapper mapper;

    @Override
    @Transactional
    public SiteDTO creer(SiteCreateDTO dto, Long createurId) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));

        Ville ville = villeRepository.findById(dto.getVilleId())
                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable"));

        Parc parc = parcRepository.findById(dto.getParcId())
                .orElseThrow(() -> new ResourceNotFoundException("Parc introuvable"));

        Site site = new Site();
        site.setClient(client);
        site.setVille(ville);
        site.setParc(parc);
        site.setAdresse(dto.getAdresse());

        return mapper.toDTO(siteRepository.save(site));
    }

    @Override
    @Transactional(readOnly = true)
    public SiteDTO getById(Long id, Utilisateur utilisateur) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site introuvable"));

        verifierAcces(site, utilisateur);

        return mapper.toDTO(site);
    }

    @Override
    @Transactional
    public SiteDTO modifier(Long id, SiteUpdateDTO dto) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site introuvable"));

        if (dto.getVilleId() != null) {
            Ville ville = villeRepository.findById(dto.getVilleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable"));
            site.setVille(ville);
        }

        if (dto.getParcId() != null) {
            Parc parc = parcRepository.findById(dto.getParcId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parc introuvable"));
            site.setParc(parc);
        }

        site.setAdresse(dto.getAdresse());

        return mapper.toDTO(siteRepository.save(site));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site introuvable"));

        if (!site.getAscenseurs().isEmpty()) {
            throw new BusinessRuleException(
                    "Impossible de supprimer un site contenant des ascenseurs.");
        }

        siteRepository.delete(site);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteDTO> listerTous() {
        return siteRepository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteDTO> listerParVille(Long villeId) {
        return siteRepository.findByVilleId(villeId).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteDTO> listerParClient(Long clientId, Utilisateur utilisateur) {
        if (utilisateur instanceof Client client && !clientId.equals(client.getId())) {
            throw new AccessDeniedException("Vous n'avez pas accès aux sites de ce client.");
        }

        return siteRepository.findByClientId(clientId).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteDTO> listerMesSites(Client client) {
        return siteRepository.findByClientId(client.getId()).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteDTO> listerSitesDeMonParc(Technicien technicien) {
        Technicien managed = technicienRepository.findByIdWithParcs(technicien.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Technicien introuvable"));

        List<Long> parcIds = managed.getParcs().stream()
                .map(Parc::getId)
                .toList();

        if (parcIds.isEmpty()) {
            return List.of();
        }

        return siteRepository.findByParcIdIn(parcIds).stream()
                .map(mapper::toDTO)
                .toList();
    }

    private void verifierAcces(Site site, Utilisateur utilisateur) {
        if (utilisateur instanceof Client client
                && !site.getClient().getId().equals(client.getId())) {
            throw new AccessDeniedException("Vous n'avez pas accès à ce site.");
        }
    }
}