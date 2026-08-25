package com.example.backend_siop.maintenance.service.impl;

import com.example.backend_siop.ascenseur.entity.Ascenseur;
import com.example.backend_siop.ascenseur.repository.AscenseurRepository;
import com.example.backend_siop.common.dto.PieceJointeAvecUrlDTO;
import com.example.backend_siop.common.entity.PieceJointe;
import com.example.backend_siop.common.enums.TypeEntiteJointe;
import com.example.backend_siop.common.exception.BusinessRuleException;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.common.repository.PieceJointeRepository;
import com.example.backend_siop.common.util.FileStorageUtil;
import com.example.backend_siop.maintenance.dto.DemandeEvaluationCreateDTO;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceCreateDTO;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceDTO;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceIntegrationCreateDTO;
import com.example.backend_siop.maintenance.dto.RejetDemandeDTO;
import com.example.backend_siop.maintenance.dto.mapper.DemandeMaintenanceMapper;
import com.example.backend_siop.maintenance.entity.DemandeMaintenance;
import com.example.backend_siop.maintenance.enums.PrioriteDemande;
import com.example.backend_siop.maintenance.enums.StatutDemande;
import com.example.backend_siop.maintenance.enums.TypeDemande;
import com.example.backend_siop.maintenance.repository.DemandeMaintenanceRepository;
import com.example.backend_siop.maintenance.service.DemandeMaintenanceService;
import com.example.backend_siop.notification.enums.TypeNotification;
import com.example.backend_siop.notification.service.NotificationService;
import com.example.backend_siop.utilisateur.entity.Client;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandeMaintenanceServiceImpl implements DemandeMaintenanceService {

    private static final int MAX_PHOTOS_PAR_DEMANDE = 5;

    private final DemandeMaintenanceRepository demandeRepository;
    private final AscenseurRepository ascenseurRepository;
    private final PieceJointeRepository pieceJointeRepository;
    private final FileStorageUtil fileStorageUtil;
    private final DemandeMaintenanceMapper mapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public DemandeMaintenanceDTO creer(DemandeMaintenanceCreateDTO dto, Client client) {
        Ascenseur ascenseur = ascenseurRepository.findById(dto.getAscenseurId())
                .orElseThrow(() -> new ResourceNotFoundException("Ascenseur introuvable"));

        if (!ascenseur.getClient().getId().equals(client.getId())) {
            throw new BusinessRuleException("Cet ascenseur ne vous appartient pas.");
        }

        DemandeMaintenance demande = mapper.toEntity(dto);
        demande.setAscenseur(ascenseur);
        demande.setClient(client);
        demande.setStatut(StatutDemande.EN_ATTENTE);

        return mapper.toDTO(demandeRepository.save(demande));
    }

    @Override
    @Transactional
    public DemandeMaintenanceDTO accepter(Long id) {
        DemandeMaintenance demande = demandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable"));

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new BusinessRuleException("Seules les demandes en attente peuvent être acceptées.");
        }
        demande.setStatut(StatutDemande.ASSIGNEE);

        return mapper.toDTO(demandeRepository.save(demande));
    }

    @Override
    @Transactional
    public DemandeMaintenanceDTO creerEvaluation(DemandeEvaluationCreateDTO dto, Client client) {
        DemandeMaintenance demande = new DemandeMaintenance();
        demande.setClient(client);
        demande.setSite(null);
        demande.setAscenseur(null);
        demande.setVilleSaisie(dto.getVille());
        demande.setAdresseSaisie(dto.getAdresse());
        demande.setTypeDemande(TypeDemande.EVALUATION);
        demande.setPriorite(PrioriteDemande.NORMALE);
        demande.setStatut(StatutDemande.EN_ATTENTE);
        demande.setDescription(dto.getDescription());
        demande.setDateSouhaitee(dto.getDateSouhaitee());

        return mapper.toDTO(demandeRepository.save(demande));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandeMaintenanceDTO> listerMesDemandes(Client client) {
        return demandeRepository.findByClientIdOrderByCreatedAtDesc(client.getId())
                .stream()
                .map(this::toDTOAvecPhotos)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DemandeMaintenanceDTO getDetail(Long id, Client client) {
        DemandeMaintenance demande = demandeRepository.findByIdAndClientId(id, client.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable"));
        return toDTOAvecPhotos(demande);
    }

    @Override
    @Transactional
    public DemandeMaintenanceDTO annuler(Long id, Client client) {
        DemandeMaintenance demande = demandeRepository.findByIdAndClientId(id, client.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable"));

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new BusinessRuleException(
                    "Impossible d'annuler une demande déjà prise en charge.");
        }

        demande.setStatut(StatutDemande.ANNULEE);
        demande.setDateResolution(LocalDateTime.now());
        return mapper.toDTO(demandeRepository.save(demande));
    }

    @Override
    @Transactional(readOnly = true)
    public void verifierPeutAjouterPhoto(Long demandeId, Client client) {
        DemandeMaintenance demande = demandeRepository.findByIdAndClientId(demandeId, client.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable"));

        long nbExistantes = pieceJointeRepository.countByEntiteTypeAndEntiteId(
                TypeEntiteJointe.DEMANDE_MAINTENANCE, demande.getId());

        if (nbExistantes >= MAX_PHOTOS_PAR_DEMANDE) {
            throw new BusinessRuleException(
                    "Nombre maximum de photos atteint (" + MAX_PHOTOS_PAR_DEMANDE + ") pour cette demande.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandeMaintenanceDTO> listerDemandesEnAttente() {
        return demandeRepository
                .findByStatutOrderByPrioriteDescCreatedAtAsc(StatutDemande.EN_ATTENTE)
                .stream()
                .map(this::toDTOAvecPhotos)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandeMaintenanceDTO> listerToutesDemandes(StatutDemande statut) {
        List<DemandeMaintenance> demandes = (statut == null)
                ? demandeRepository.findAll()
                : demandeRepository.findByStatutOrderByPrioriteDescCreatedAtAsc(statut);

        return demandes.stream()
                .map(this::toDTOAvecPhotos)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DemandeMaintenanceDTO getDetailPourResponsable(Long id) {
        DemandeMaintenance demande = demandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable"));
        return toDTOAvecPhotos(demande);
    }

    @Override
    @Transactional
    public DemandeMaintenanceDTO rejeter(Long id, RejetDemandeDTO dto) {
        DemandeMaintenance demande = demandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable"));

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new BusinessRuleException("Seule une demande en attente peut être rejetée.");
        }

        demande.setStatut(StatutDemande.REJETEE);
        demande.setDateResolution(LocalDateTime.now());
        demande.setMotifRejet(dto.getMotif());
        DemandeMaintenance saved = demandeRepository.save(demande);

        notificationService.creer(
                demande.getClient(),
                TypeNotification.STATUT_DEMANDE_CHANGE,
                "Demande rejetée",
                "Votre demande a été rejetée. Motif : " + dto.getMotif(),
                "DEMANDE_MAINTENANCE",
                demande.getId()
        );

        return mapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DemandeMaintenance getEntitePourResponsable(Long id) {
        return demandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable"));
    }

    // 🔥 Méthode d'intégration n8n (HEAD)
    @Override
    @Transactional
    public DemandeMaintenanceDTO creerDepuisIntegration(DemandeMaintenanceIntegrationCreateDTO dto, Utilisateur createur) {

        // 1. Si un ascenseur est fourni, on le vérifie
        Ascenseur ascenseur = null;
        Client client = null;
        Site site = null;

        if (dto.getAscenseurId() != null) {
            ascenseur = ascenseurRepository.findById(dto.getAscenseurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ascenseur introuvable"));
            client = ascenseur.getClient();
            site = ascenseur.getSiteEntity();
        }

        // 2. Création de la demande
        DemandeMaintenance demande = new DemandeMaintenance();
        demande.setAscenseur(ascenseur);
        demande.setClient(client); // peut être null si ascenseur non trouvé
        demande.setSite(site);
        demande.setTypeDemande(dto.getTypeDemande());
        demande.setPriorite(dto.getPriorite());
        demande.setDescription(dto.getDescription());
        demande.setDateSouhaitee(dto.getDateSouhaitee());
        demande.setStatut(StatutDemande.EN_ATTENTE);

        // 3. Si l'ascenseur n'est pas connu, on stocke l'adresse libre
        if (ascenseur == null) {
            demande.setVilleSaisie(dto.getVilleLibre());
            demande.setAdresseSaisie(dto.getAdresseLibre());
        }

        // 4. Sauvegarde
        DemandeMaintenance saved = demandeRepository.save(demande);

        // 5. Notification au responsable (optionnel mais recommandé)
        // notificationService.creer(...)

        // 6. Retour du DTO
        return mapper.toDTO(saved);
    }

    private DemandeMaintenanceDTO toDTOAvecPhotos(DemandeMaintenance demande) {
        DemandeMaintenanceDTO dto = mapper.toDTO(demande);

        List<PieceJointe> photos = pieceJointeRepository.findByEntiteTypeAndEntiteId(
                TypeEntiteJointe.DEMANDE_MAINTENANCE, demande.getId());

        List<PieceJointeAvecUrlDTO> photosAvecUrl = photos.stream()
                .map(p -> new PieceJointeAvecUrlDTO(
                        p.getId(),
                        p.getNomFichier(),
                        fileStorageUtil.getUrlTemporaire(p.getCheminFichier()),
                        p.getTypeFichier(),
                        p.getDescription()
                ))
                .toList();

        dto.setPhotos(photosAvecUrl);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandeMaintenanceDTO> listerDemandesAtraiter() {
        return demandeRepository
                .findByStatutOrderByPrioriteDescCreatedAtAsc(StatutDemande.EN_ATTENTE)
                .stream()
                .limit(5)
                .map(this::toDTOAvecPhotos)
                .toList();
    }
}
