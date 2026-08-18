package com.example.backend_siop.ascenseur.service.impl;

import com.example.backend_siop.ascenseur.dto.AscenseurRechercheDTO;
import com.example.backend_siop.ascenseur.dto.Ascenseur.AscenseurCreateDTO;
import com.example.backend_siop.ascenseur.dto.Ascenseur.AscenseurDTO;
import com.example.backend_siop.ascenseur.dto.Ascenseur.AscenseurUpdateDTO;
import com.example.backend_siop.common.dto.PieceJointeResponseDTO;
import com.example.backend_siop.ascenseur.dto.mapperAscenseur.AscenseurMapper;
import com.example.backend_siop.ascenseur.entity.Ascenseur;
import com.example.backend_siop.ascenseur.entity.Site;
import com.example.backend_siop.ascenseur.repository.AscenseurRepository;
import com.example.backend_siop.ascenseur.repository.SiteRepository;
import com.example.backend_siop.ascenseur.service.AscenseurService;
import com.example.backend_siop.common.entity.PieceJointe;
import com.example.backend_siop.common.enums.TypeEntiteJointe;
import com.example.backend_siop.common.exception.BusinessRuleException;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.common.repository.PieceJointeRepository;
import com.example.backend_siop.common.util.FileStorageUtil;
import com.example.backend_siop.utilisateur.entity.Client;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import com.example.backend_siop.utilisateur.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AscenseurServiceImpl implements AscenseurService {

    private final AscenseurRepository ascenseurRepository;
    private final SiteRepository siteRepository;
    private final PieceJointeRepository pieceJointeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FileStorageUtil fileStorageUtil;
    private final AscenseurMapper mapper;
    private final ArborescenceService arborescenceService;

    @Override
    @Transactional
    public AscenseurDTO creer(AscenseurCreateDTO dto, Long createurId) {
        Client client = (Client) utilisateurRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));

        if (dto.getNumeroSerie() != null && !dto.getNumeroSerie().isBlank()
                && ascenseurRepository.existsByNumeroSerie(dto.getNumeroSerie())) {
            throw new BusinessRuleException("Un ascenseur avec ce numéro de série existe déjà.");
        }

        Ascenseur ascenseur = new Ascenseur();
        ascenseur.setClient(client);
        ascenseur.setNom(dto.getNom());
        ascenseur.setDescription(dto.getDescription());
        ascenseur.setCoutAcquisition(dto.getCoutAcquisition());
        ascenseur.setFabricant(dto.getFabricant());
        ascenseur.setModele(dto.getModele());
        ascenseur.setMarque(dto.getMarque());
        ascenseur.setNombreEtages(dto.getNombreEtages());
        ascenseur.setCapacitePersonnes(dto.getCapacitePersonnes());
        ascenseur.setChargeMaxKg(dto.getChargeMaxKg());
        ascenseur.setVitesse(dto.getVitesse());
        ascenseur.setNumeroSerie(dto.getNumeroSerie());
        ascenseur.setCodeBarre(dto.getCodeBarre());
        ascenseur.setPuissance(dto.getPuissance());
        ascenseur.setType(dto.getType());
        ascenseur.setSiteEntity(siteRepository.findById(dto.getSiteId())
                .orElseThrow(() -> new ResourceNotFoundException("Site introuvable")));
        ascenseur.setDateMiseEnService(
                dto.getDateMiseEnService() != null ? dto.getDateMiseEnService() : LocalDate.now());
        ascenseur.setDateExpirationGarantie(dto.getDateExpirationGarantie());
        ascenseur.setInformationsSupplementaires(dto.getInformationsSupplementaires());

        Ascenseur saved = ascenseurRepository.save(ascenseur);
        arborescenceService.genererArborescence(saved);

        AscenseurDTO result = mapper.toDTO(saved);
        result.setPiecesJointes(List.of());
        return result;
    }

    @Override
    public AscenseurDTO getById(Long id) {
        Ascenseur ascenseur = ascenseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ascenseur introuvable"));

        AscenseurDTO dto = mapper.toDTO(ascenseur);
        dto.setPiecesJointes(chargerPiecesJointes(id));

        return dto;
    }

    @Override
    public List<AscenseurDTO> listerParClient(Long clientId) {
        return ascenseurRepository.findByClientId(clientId).stream()
                .map(ascenseur -> {
                    AscenseurDTO dto = mapper.toDTO(ascenseur);
                    dto.setPiecesJointes(chargerPiecesJointes(ascenseur.getId()));
                    return dto;
                })
                .toList();
    }

    @Override
    public List<AscenseurDTO> listerTous() {
        return ascenseurRepository.findAll().stream()
                .map(ascenseur -> {
                    AscenseurDTO dto = mapper.toDTO(ascenseur);
                    dto.setPiecesJointes(chargerPiecesJointes(ascenseur.getId()));
                    return dto;
                })
                .toList();
    }

    @Override
    public List<AscenseurDTO> listerParSite(Long siteId, Utilisateur utilisateur) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site introuvable"));

        if (utilisateur instanceof Client client
                && !site.getClient().getId().equals(client.getId())) {
            throw new AccessDeniedException("Vous n'avez pas accès à ce site.");
        }

        return ascenseurRepository.findBySiteEntityId(siteId).stream()
                .map(ascenseur -> {
                    AscenseurDTO dto = mapper.toDTO(ascenseur);
                    dto.setPiecesJointes(chargerPiecesJointes(ascenseur.getId()));
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional
    public AscenseurDTO modifier(Long id, AscenseurUpdateDTO dto) {
        Ascenseur ascenseur = ascenseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ascenseur introuvable"));

        if (dto.getNumeroSerie() != null && !dto.getNumeroSerie().isBlank()
                && !dto.getNumeroSerie().equals(ascenseur.getNumeroSerie())
                && ascenseurRepository.existsByNumeroSerie(dto.getNumeroSerie())) {
            throw new BusinessRuleException("Un ascenseur avec ce numéro de série existe déjà.");
        }

        ascenseur.setNom(dto.getNom());
        ascenseur.setDescription(dto.getDescription());
        ascenseur.setCoutAcquisition(dto.getCoutAcquisition());
        ascenseur.setFabricant(dto.getFabricant());
        ascenseur.setMarque(dto.getMarque());
        ascenseur.setModele(dto.getModele());
        ascenseur.setNumeroSerie(dto.getNumeroSerie());
        ascenseur.setCodeBarre(dto.getCodeBarre());
        ascenseur.setPuissance(dto.getPuissance());
        ascenseur.setNombreEtages(dto.getNombreEtages());
        ascenseur.setCapacitePersonnes(dto.getCapacitePersonnes());
        ascenseur.setChargeMaxKg(dto.getChargeMaxKg());
        ascenseur.setVitesse(dto.getVitesse());
        ascenseur.setType(dto.getType());
        ascenseur.setDateMiseEnService(dto.getDateMiseEnService());
        ascenseur.setDateExpirationGarantie(dto.getDateExpirationGarantie());
        ascenseur.setInformationsSupplementaires(dto.getInformationsSupplementaires());
        ascenseur.setSiteEntity(siteRepository.findById(dto.getSiteId())
                .orElseThrow(() -> new ResourceNotFoundException("Site introuvable")));



        Ascenseur saved = ascenseurRepository.save(ascenseur);

        AscenseurDTO result = mapper.toDTO(saved);
        result.setPiecesJointes(chargerPiecesJointes(id));
        return result;
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        Ascenseur ascenseur = ascenseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ascenseur introuvable"));

        List<PieceJointe> pieces = pieceJointeRepository.findByEntiteTypeAndEntiteId(
                TypeEntiteJointe.ASCENSEUR, id);
        pieces.forEach(p -> fileStorageUtil.delete(p.getCheminFichier()));
        pieceJointeRepository.deleteByEntiteTypeAndEntiteId(TypeEntiteJointe.ASCENSEUR, id);

        ascenseurRepository.delete(ascenseur);
    }

    private List<PieceJointeResponseDTO> chargerPiecesJointes(Long ascenseurId) {
        List<PieceJointe> pieces = pieceJointeRepository.findByEntiteTypeAndEntiteId(
                TypeEntiteJointe.ASCENSEUR, ascenseurId);

        return pieces.stream()
                .map(p -> {
                    PieceJointeResponseDTO d = new PieceJointeResponseDTO();
                    d.setId(p.getId());
                    d.setNomFichier(p.getNomFichier());
                    d.setTypeFichier(p.getTypeFichier().name());
                    d.setDescription(p.getDescription());
                    d.setUrl(fileStorageUtil.getUrlTemporaire(p.getCheminFichier()));
                    return d;
                })
                .toList();
    }

    @Override
    @Transactional
    public AscenseurDTO basculerStatut(Long id) {
        Ascenseur ascenseur = ascenseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ascenseur introuvable"));

        ascenseur.setActif(!ascenseur.isActif());

        Ascenseur saved = ascenseurRepository.save(ascenseur);

        AscenseurDTO result = mapper.toDTO(saved);
        result.setPiecesJointes(chargerPiecesJointes(id));
        return result;
    }


    @Override
    @Transactional(readOnly = true)
    public List<AscenseurRechercheDTO> rechercherParTexteLibre(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }

        String normalized = query.trim();
        List<Ascenseur> resultats = ascenseurRepository.rechercherParTexteLibre(normalized);

        return resultats.stream()
                .map(a -> {
                    String adresseComplete = "";
                    String ville = "";
                    if (a.getSiteEntity() != null) {
                        Site s = a.getSiteEntity();
                        adresseComplete = s.getAdresse();
                        if (s.getVille() != null) {
                            ville = s.getVille().getNom();
                            adresseComplete += ", " + ville;
                        }
                    }
                    // Score simple : on peut l'affiner plus tard
                    int score = calculerScore(a, normalized);
                    return new AscenseurRechercheDTO(
                            a.getId(),
                            a.getNom(),
                            adresseComplete,
                            ville,
                            a.getNumeroSerie(),
                            score
                    );
                })
                .sorted((a, b) -> b.getScore().compareTo(a.getScore()))
                .toList();
    }

    private int calculerScore(Ascenseur a, String query) {
        String lowerQuery = query.toLowerCase();
        int score = 0;

        if (a.getNumeroSerie() != null && a.getNumeroSerie().toLowerCase().contains(lowerQuery)) {
            score += 50;
        }
        if (a.getCodeBarre() != null && a.getCodeBarre().toLowerCase().contains(lowerQuery)) {
            score += 40;
        }
        if (a.getNom() != null && a.getNom().toLowerCase().contains(lowerQuery)) {
            score += 30;
        }
        if (a.getSiteEntity() != null) {
            Site s = a.getSiteEntity();
            if (s.getAdresse() != null && s.getAdresse().toLowerCase().contains(lowerQuery)) {
                score += 25;
            }
            if (s.getVille() != null && s.getVille().getNom().toLowerCase().contains(lowerQuery)) {
                score += 20;
            }
            if (s.getClient() != null && s.getClient().getNom() != null
                    && s.getClient().getNom().toLowerCase().contains(lowerQuery)) {
                score += 15;
            }
        }
        return Math.min(score, 100);
    }
    
}