package com.example.backend_siop.maintenance.service.impl;

import com.example.backend_siop.ascenseur.entity.Ascenseur;
import com.example.backend_siop.ascenseur.entity.Site;
import com.example.backend_siop.ascenseur.repository.AscenseurRepository;
import com.example.backend_siop.ascenseur.repository.SiteRepository;
import com.example.backend_siop.common.dto.PieceJointeAvecUrlDTO;
import com.example.backend_siop.common.entity.PieceJointe;
import com.example.backend_siop.common.enums.TypeEntiteJointe;
import com.example.backend_siop.common.exception.BusinessRuleException;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.common.repository.PieceJointeRepository;
import com.example.backend_siop.common.util.FileStorageUtil;
import com.example.backend_siop.maintenance.dto.BonTravailCreateDTO;
import com.example.backend_siop.maintenance.dto.BonTravailDTO;
import com.example.backend_siop.maintenance.dto.BonTravailIntegrationCreateDTO;
import com.example.backend_siop.maintenance.dto.BonTravailResumeDTO;
import com.example.backend_siop.maintenance.dto.ClotureBonTravailDTO;
import com.example.backend_siop.maintenance.dto.ConflitTechnicienDTO;
import com.example.backend_siop.maintenance.dto.mapper.BonTravailMapper;
import com.example.backend_siop.maintenance.entity.BonTravail;
import com.example.backend_siop.maintenance.entity.ChecklistMaintenance;
import com.example.backend_siop.maintenance.entity.DemandeMaintenance;
import com.example.backend_siop.maintenance.entity.ItemCheckList;
import com.example.backend_siop.maintenance.entity.ModeleChecklist;
import com.example.backend_siop.maintenance.enums.PrioriteDemande;
import com.example.backend_siop.maintenance.enums.StatutBonTravail;
import com.example.backend_siop.maintenance.enums.StatutDemande;
import com.example.backend_siop.maintenance.enums.StatutItem;
import com.example.backend_siop.maintenance.enums.TypeDemande;
import com.example.backend_siop.maintenance.repository.BonTravailRepository;
import com.example.backend_siop.maintenance.repository.ChecklistMaintenanceRepository;
import com.example.backend_siop.maintenance.repository.DemandeMaintenanceRepository;
import com.example.backend_siop.maintenance.repository.ItemCheckListRepository;
import com.example.backend_siop.maintenance.repository.ModeleChecklistRepository;
import com.example.backend_siop.maintenance.service.BonTravailService;
import com.example.backend_siop.notification.enums.TypeNotification;
import com.example.backend_siop.notification.service.NotificationService;
import com.example.backend_siop.parc.entity.Parc;
import com.example.backend_siop.parc.repository.ParcRepository;
import com.example.backend_siop.utilisateur.dto.TechnicienResumeDTO;
import com.example.backend_siop.utilisateur.dto.mapper.TechnicienMapper;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import com.example.backend_siop.utilisateur.repository.TechnicienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BonTravailServiceImpl implements BonTravailService {

    private static final List<StatutBonTravail> STATUTS_ACTIFS =
            List.of(StatutBonTravail.PLANIFIE, StatutBonTravail.EN_COURS);

    private final BonTravailRepository bonTravailRepository;
    private final DemandeMaintenanceRepository demandeRepository;
    private final AscenseurRepository ascenseurRepository;
    private final TechnicienRepository technicienRepository;
    private final PieceJointeRepository pieceJointeRepository;
    private final FileStorageUtil fileStorageUtil;
    private final BonTravailMapper mapper;
    private final TechnicienMapper technicienMapper;
    private final ChecklistMaintenanceRepository checklistRepository;
    private final ModeleChecklistRepository modeleChecklistRepository;
    private final ItemCheckListRepository itemCheckListRepository;
    private final ParcRepository parcRepository;
    private final SiteRepository siteRepository;
    private final NotificationService notificationService;

    @Value("${integration.fallback.technician-email}")
    private String fallbackTechnicianEmail;

    @Override
    @Transactional
    public BonTravailDTO creer(BonTravailCreateDTO dto, Utilisateur creePar) {
        DemandeMaintenance demande = null;
        Ascenseur ascenseur = null;
        Parc parcAscenseur = null;
        Site siteSelectionne = null;
        PrioriteDemande priorite = dto.getPriorite();

        if (dto.getDemandeMaintenanceId() != null) {
            demande = demandeRepository.findById(dto.getDemandeMaintenanceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable"));

            if (demande.getStatut() != StatutDemande.EN_ATTENTE && demande.getStatut() != StatutDemande.ASSIGNEE) {
                throw new BusinessRuleException("Cette demande a déjà été traitée (résolue, rejetée ou annulée).");
            }

            ascenseur = demande.getAscenseur();

            if (priorite == null) {
                priorite = demande.getPriorite();
            }
        } else if (dto.getAscenseurId() != null) {
            ascenseur = ascenseurRepository.findById(dto.getAscenseurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ascenseur introuvable"));

            if (priorite == null) {
                throw new BusinessRuleException("La priorité est obligatoire pour une planification directe.");
            }
        } else {
            throw new BusinessRuleException("Une demande ou un ascenseur doit être précisé.");
        }

        if (ascenseur != null) {
            if (ascenseur.getSiteEntity() == null || ascenseur.getSiteEntity().getParc() == null) {
                throw new BusinessRuleException("L'ascenseur sélectionné n'est rattaché à aucun site ou parc valide.");
            }
            parcAscenseur = ascenseur.getSiteEntity().getParc();
        } else if (demande != null && demande.getSite() != null && demande.getSite().getParc() != null) {
            parcAscenseur = demande.getSite().getParc();
        } else if (dto.getSiteId() != null) {
            siteSelectionne = siteRepository.findById(dto.getSiteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Site introuvable"));
            if (siteSelectionne.getParc() == null) {
                throw new BusinessRuleException("Ce site n'est rattaché à aucun parc.");
            }
            parcAscenseur = siteSelectionne.getParc();
        } else if (dto.getParcId() != null) {
            parcAscenseur = parcRepository.findById(dto.getParcId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parc introuvable"));
        } else {
            throw new BusinessRuleException("Cette demande d'évaluation n'est rattachée à aucun site. Veuillez sélectionner un site correspondant.");
        }

        Technicien responsable = technicienRepository.findByIdWithParcs(dto.getTechnicienResponsableId())
                .orElseThrow(() -> new ResourceNotFoundException("Technicien responsable introuvable"));
        verifierCouvreParc(responsable, parcAscenseur);

        List<Technicien> renfort = (dto.getTechnicienIdsRenfort() == null || dto.getTechnicienIdsRenfort().isEmpty())
                ? List.of()
                : technicienRepository.findAllById(dto.getTechnicienIdsRenfort());

        for (Technicien t : renfort) {
            Technicien managed = technicienRepository.findByIdWithParcs(t.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Technicien introuvable"));
            verifierCouvreParc(managed, parcAscenseur);
        }

        List<Technicien> equipeComplete = new ArrayList<>();
        equipeComplete.add(responsable);
        equipeComplete.addAll(renfort);

        LocalDateTime debut = dto.getDateInterventionPrevue();
        LocalDateTime fin = debut.plusMinutes(dto.getDureeEstimeeMinutes());

        for (Technicien t : equipeComplete) {
            boolean enConflit = bonTravailRepository
                    .findByTechniciensContainingAndStatutIn(t, STATUTS_ACTIFS)
                    .stream()
                    .anyMatch(b -> chevauche(b, debut, fin));
            if (enConflit) {
                throw new BusinessRuleException(
                        "Le technicien " + t.getNom() + " a déjà une intervention sur ce créneau.");
            }
        }

        BonTravail bonTravail = new BonTravail();
        bonTravail.setDemandeMaintenance(demande);
        bonTravail.setAscenseur(ascenseur);
        bonTravail.setTechnicienResponsable(responsable);
        bonTravail.setTechniciens(equipeComplete);
        bonTravail.setStatut(StatutBonTravail.PLANIFIE);
        bonTravail.setPriorite(priorite);
        bonTravail.setDateInterventionPrevue(debut);
        bonTravail.setDureeEstimeeMinutes(dto.getDureeEstimeeMinutes());
        bonTravail.setDescription(dto.getDescription());
        bonTravail.setCreePar(creePar);

        BonTravail saved = bonTravailRepository.save(bonTravail);

        for (Technicien t : equipeComplete) {
            notificationService.creer(
                    t,
                    TypeNotification.NOUVEAU_TRAVAIL_ASSIGNE,
                    "Nouveau travail assigné",
                    "Vous avez été assigné à une intervention prévue le " + debut,
                    "BON_TRAVAIL",
                    saved.getId()
            );
        }

        if (demande != null) {
            demande.setStatut(StatutDemande.ASSIGNEE);
            if (siteSelectionne != null && demande.getSite() == null) {
                demande.setSite(siteSelectionne);
            }
            demandeRepository.save(demande);

            notificationService.creer(
                    demande.getClient(),
                    TypeNotification.STATUT_DEMANDE_CHANGE,
                    "Votre demande a été prise en charge",
                    "Une intervention a été planifiée le " + debut + ".",
                    "DEMANDE_MAINTENANCE",
                    demande.getId()
            );
        }

        boolean estPreventif = dto.isVisitePreventive()
                || (demande != null && demande.getTypeDemande() == TypeDemande.ENTRETIEN_PREVENTIF);

        if (estPreventif && ascenseur != null) {
            genererChecklist(saved, ascenseur, debut);
        }

        return toDTOAvecPhotos(saved);
    }

    private void genererChecklist(BonTravail bonTravail, Ascenseur ascenseur, LocalDateTime dateIntervention) {
        int mois = dateIntervention.getMonthValue();
        int annee = dateIntervention.getYear();

        if (checklistRepository.existsByAscenseurIdAndMoisAndAnnee(ascenseur.getId(), mois, annee)) {
            throw new BusinessRuleException("Une checklist existe déjà pour cet ascenseur ce mois-ci.");
        }

        ChecklistMaintenance checklist = new ChecklistMaintenance();
        checklist.setAscenseur(ascenseur);
        checklist.setBonTravail(bonTravail);
        checklist.setMois(mois);
        checklist.setAnnee(annee);
        checklistRepository.save(checklist);

        List<ModeleChecklist> itemsDuMois = modeleChecklistRepository.findAllByOrderByOrdreAsc()
                .stream()
                .filter(m -> m.getMoisApplicables().contains(mois))
                .toList();

        for (ModeleChecklist modele : itemsDuMois) {
            ItemCheckList item = new ItemCheckList();
            item.setChecklistMaintenance(checklist);
            item.setLibelle(modele.getLibelle());
            item.setOrdre(modele.getOrdre());
            item.setStatut(StatutItem.NON_VERIFIE);
            itemCheckListRepository.save(item);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BonTravailResumeDTO> lister() {
        return bonTravailRepository.findAllByOrderByStatutAscDateInterventionPrevueAsc()
                .stream()
                .map(mapper::toResumeDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BonTravailDTO getDetail(Long id) {
        BonTravail bonTravail = bonTravailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon de travail introuvable"));
        return toDTOAvecPhotos(bonTravail);
    }

    @Override
    @Transactional
    public BonTravailDTO annuler(Long id) {
        BonTravail bonTravail = bonTravailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon de travail introuvable"));

        if (bonTravail.getStatut() == StatutBonTravail.TERMINE) {
            throw new BusinessRuleException("Impossible d'annuler un bon de travail déjà terminé.");
        }

        bonTravail.setStatut(StatutBonTravail.ANNULE);
        return toDTOAvecPhotos(bonTravailRepository.save(bonTravail));
    }

    private static final List<StatutBonTravail> STATUTS_MES_INTERVENTIONS =
            List.of(StatutBonTravail.PLANIFIE, StatutBonTravail.EN_COURS, StatutBonTravail.TERMINE);

    @Override
    @Transactional(readOnly = true)
    public List<BonTravailResumeDTO> listerMesInterventions(Technicien technicien) {
        return bonTravailRepository
                .findByTechniciensContainingAndStatutIn(technicien, STATUTS_MES_INTERVENTIONS)
                .stream()
                .sorted((a, b) -> a.getDateInterventionPrevue().compareTo(b.getDateInterventionPrevue()))
                .map(mapper::toResumeDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BonTravailDTO getDetailPourTechnicien(Long id, Technicien technicien) {
        BonTravail bonTravail = bonTravailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon de travail introuvable"));

        boolean faitPartieEquipe = bonTravail.getTechniciens().stream()
                .anyMatch(t -> t.getId().equals(technicien.getId()));

        if (!faitPartieEquipe) {
            throw new ResourceNotFoundException("Bon de travail introuvable");
        }

        return toDTOAvecPhotos(bonTravail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConflitTechnicienDTO> verifierDisponibilite(
            List<Long> technicienIds, LocalDateTime debut, int dureeMinutes) {

        LocalDateTime fin = debut.plusMinutes(dureeMinutes);
        List<ConflitTechnicienDTO> conflits = new ArrayList<>();

        for (Long id : technicienIds) {
            Technicien technicien = technicienRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Technicien introuvable"));

            List<BonTravail> bonsEnConflit = bonTravailRepository
                    .findByTechniciensContainingAndStatutIn(technicien, STATUTS_ACTIFS)
                    .stream()
                    .filter(b -> chevauche(b, debut, fin))
                    .toList();

            for (BonTravail b : bonsEnConflit) {
                conflits.add(new ConflitTechnicienDTO(
                        technicien.getId(),
                        technicien.getNom(),
                        b.getId(),
                        b.getAscenseur() != null ? b.getAscenseur().getNom() : "Inconnu",
                        b.getDateInterventionPrevue().toString()
                ));
            }
        }

        return conflits;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnicienResumeDTO> listerTechniciensDisponibles(
            Long ascenseurId, LocalDateTime debut, int dureeMinutes) {

        Ascenseur ascenseur = ascenseurRepository.findById(ascenseurId)
                .orElseThrow(() -> new ResourceNotFoundException("Ascenseur introuvable"));

        Parc parc = ascenseur.getSiteEntity().getParc();
        LocalDateTime fin = debut.plusMinutes(dureeMinutes);

        List<Technicien> techniciensDuParc = technicienRepository.findByParcs_Id(parc.getId());

        return techniciensDuParc.stream()
                .filter(t -> {
                    boolean enConflit = bonTravailRepository
                            .findByTechniciensContainingAndStatutIn(t, STATUTS_ACTIFS)
                            .stream()
                            .anyMatch(b -> chevauche(b, debut, fin));
                    return !enConflit;
                })
                .map(technicienMapper::toResumeDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnicienResumeDTO> listerTechniciensDisponiblesParSite(
            Long siteId, LocalDateTime debut, int dureeMinutes) {
        
        // Cette méthode est une implémentation simplifiée. Dans un vrai projet, vous filtreriez par site.
        return technicienRepository.findAll()
                .stream()
                .filter(t -> {
                    // Ici vous pouvez ajouter une logique de vérification de disponibilité si nécessaire
                    return true;
                })
                .map(technicienMapper::toResumeDTO)
                .toList();
    }

    private void verifierCouvreParc(Technicien technicien, Parc parc) {
        boolean couvre = technicien.getParcs().stream()
                .anyMatch(p -> p.getId().equals(parc.getId()));
        if (!couvre) {
            throw new BusinessRuleException(
                    "Le technicien " + technicien.getNom() + " ne couvre pas le parc de cet ascenseur.");
        }
    }

    private boolean chevauche(BonTravail existant, LocalDateTime debutPropose, LocalDateTime finProposee) {
        LocalDateTime debutExistant = existant.getDateInterventionPrevue();
        LocalDateTime finExistant = debutExistant.plusMinutes(existant.getDureeEstimeeMinutes());
        return debutExistant.isBefore(finProposee) && debutPropose.isBefore(finExistant);
    }

    private String nomOuFallback(BonTravail bonTravail) {
        if (bonTravail.getAscenseur() != null) {
            return bonTravail.getAscenseur().getNom();
        }
        return "l'installation en cours d'évaluation";
    }

    private BonTravailDTO toDTOAvecPhotos(BonTravail bonTravail) {
        BonTravailDTO dto = mapper.toDTO(bonTravail);

        List<PieceJointeAvecUrlDTO> photosDemande = bonTravail.getDemandeMaintenance() != null
                ? recupererPieces(TypeEntiteJointe.DEMANDE_MAINTENANCE, bonTravail.getDemandeMaintenance().getId())
                : List.of();

        List<PieceJointeAvecUrlDTO> piecesBonTravail =
                recupererPieces(TypeEntiteJointe.BON_TRAVAIL, bonTravail.getId());

        dto.setPhotosDemande(photosDemande);
        dto.setPiecesJointesBonTravail(piecesBonTravail);
        return dto;
    }

    private List<PieceJointeAvecUrlDTO> recupererPieces(TypeEntiteJointe type, Long entiteId) {
        List<PieceJointe> pieces = pieceJointeRepository.findByEntiteTypeAndEntiteId(type, entiteId);
        return pieces.stream()
                .map(p -> new PieceJointeAvecUrlDTO(
                        p.getId(),
                        p.getNomFichier(),
                        fileStorageUtil.getUrlTemporaire(p.getCheminFichier()),
                        p.getTypeFichier(),
                        p.getDescription()
                ))
                .toList();
    }

    @Override
    @Transactional
    public BonTravailDTO demarrer(Long id, Technicien technicien) {
        BonTravail bonTravail = bonTravailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon de travail introuvable"));

        boolean faitPartieEquipe = bonTravail.getTechniciens().stream()
                .anyMatch(t -> t.getId().equals(technicien.getId()));
        if (!faitPartieEquipe) {
            throw new BusinessRuleException("Vous ne faites pas partie de l'équipe assignée.");
        }

        if (checklistRepository.findByBonTravailId(id).isPresent()) {
            throw new BusinessRuleException("Ce bon possède une checklist : démarrez-la via /api/checklists/{id}/demarrer.");
        }

        if (bonTravail.getStatut() != StatutBonTravail.PLANIFIE) {
            throw new BusinessRuleException("Seul un bon planifié peut être démarré.");
        }

        bonTravail.setStatut(StatutBonTravail.EN_COURS);
        bonTravail.setDateDebutReelle(LocalDateTime.now());

        DemandeMaintenance demande = bonTravail.getDemandeMaintenance();
        if (demande != null) {
            demande.setStatut(StatutDemande.EN_COURS);
            demandeRepository.save(demande);

            String cible = bonTravail.getAscenseur() != null
                    ? bonTravail.getAscenseur().getNom()
                    : "votre installation";

            notificationService.creer(
                    demande.getClient(),
                    TypeNotification.STATUT_DEMANDE_CHANGE,
                    "Intervention démarrée",
                    "Le technicien a démarré l'intervention sur " + cible + ".",
                    "DEMANDE_MAINTENANCE",
                    demande.getId()
            );
        }

        return toDTOAvecPhotos(bonTravailRepository.save(bonTravail));
    }

    @Override
    @Transactional
    public BonTravailDTO terminer(Long id, ClotureBonTravailDTO dto) {
        BonTravail bonTravail = bonTravailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon de travail introuvable"));

        if (checklistRepository.findByBonTravailId(id).isPresent()) {
            throw new BusinessRuleException("Ce bon possède une checklist : clôturez-la via /api/checklists/{id}/cloturer.");
        }

        if (bonTravail.getStatut() != StatutBonTravail.EN_COURS) {
            throw new BusinessRuleException("Seul un bon en cours peut être clôturé.");
        }

        bonTravail.setDiagnostic(dto.getDiagnostic());
        bonTravail.setCauseIdentifiee(dto.getCauseIdentifiee());
        bonTravail.setActionRealisee(dto.getActionRealisee());
        bonTravail.setPiecesRemplacees(dto.getPiecesRemplacees());
        bonTravail.setEssaiConcluant(dto.getEssaiConcluant());
        bonTravail.setRecommandations(dto.getRecommandations());
        bonTravail.setDateFinReelle(LocalDateTime.now());
        bonTravail.setStatut(StatutBonTravail.TERMINE);

        String cible = nomOuFallback(bonTravail);

        if (bonTravail.getCreePar() != null) {
            notificationService.creer(
                    bonTravail.getCreePar(),
                    TypeNotification.TRAVAIL_TERMINE,
                    "Travail terminé",
                    "L'intervention sur " + cible + " a été clôturée.",
                    "BON_TRAVAIL",
                    bonTravail.getId()
            );
        }

        DemandeMaintenance demande = bonTravail.getDemandeMaintenance();
        if (demande != null) {
            demande.setStatut(StatutDemande.RESOLUE);
            demandeRepository.save(demande);

            notificationService.creer(
                    demande.getClient(),
                    TypeNotification.STATUT_DEMANDE_CHANGE,
                    "Demande résolue",
                    "Votre demande concernant " + cible + " a été résolue.",
                    "DEMANDE_MAINTENANCE",
                    demande.getId()
            );
        }

        return toDTOAvecPhotos(bonTravailRepository.save(bonTravail));
    }

    // 🔥 Méthode d'intégration n8n (HEAD)
    @Override
    @Transactional
    public BonTravailDTO creerIntegration(BonTravailIntegrationCreateDTO dto, Utilisateur creePar) {

        // --- 1. Validation minimale ---
        if (dto.getAscenseurId() == null &&
                (dto.getAdresseLibre() == null || dto.getAdresseLibre().isBlank())) {
            throw new BusinessRuleException(
                    "Pour une intervention externe, veuillez fournir soit un ascenseurId, soit une adresse libre."
            );
        }

        // --- 2. Gestion de la priorité ---
        PrioriteDemande priorite = dto.getPriorite();
        if (dto.isEstUrgence()) {
            priorite = PrioriteDemande.URGENTE;
        } else if (priorite == null) {
            priorite = PrioriteDemande.NORMALE;
        }

        // --- 3. Récupération de l'ascenseur (si fourni) ---
        Ascenseur ascenseur = null;
        Parc parcAscenseur = null;
        if (dto.getAscenseurId() != null) {
            ascenseur = ascenseurRepository.findById(dto.getAscenseurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ascenseur introuvable"));
            if (ascenseur.getSiteEntity() != null && ascenseur.getSiteEntity().getParc() != null) {
                parcAscenseur = ascenseur.getSiteEntity().getParc();
            }
        }

        // --- 4. Gestion du technicien responsable (fallback si absent) ---
        Technicien responsable;
        if (dto.getTechnicienResponsableId() != null) {
            responsable = technicienRepository.findByIdWithParcs(dto.getTechnicienResponsableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Technicien responsable introuvable"));
            if (parcAscenseur != null) {
                verifierCouvreParc(responsable, parcAscenseur);
            }
        } else {
            // Fallback : technicien de secours
            responsable = technicienRepository.findByEmail(fallbackTechnicianEmail)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Aucun technicien fallback configuré. Veuillez définir 'integration.fallback.technician-email'."
                    ));
        }

        // --- 5. Gestion des techniciens renfort ---
        List<Technicien> renfort = (dto.getTechnicienIdsRenfort() == null || dto.getTechnicienIdsRenfort().isEmpty())
                ? List.of()
                : technicienRepository.findAllById(dto.getTechnicienIdsRenfort());

        for (Technicien t : renfort) {
            Technicien managed = technicienRepository.findByIdWithParcs(t.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Technicien introuvable"));
            if (parcAscenseur != null) {
                verifierCouvreParc(managed, parcAscenseur);
            }
        }

        // --- 6. Conflits de planning ---
        List<Technicien> equipeComplete = new ArrayList<>();
        equipeComplete.add(responsable);
        equipeComplete.addAll(renfort);

        LocalDateTime debut = dto.getDateInterventionPrevue();
        LocalDateTime fin = debut.plusMinutes(dto.getDureeEstimeeMinutes());

        for (Technicien t : equipeComplete) {
            boolean enConflit = bonTravailRepository
                    .findByTechniciensContainingAndStatutIn(t, STATUTS_ACTIFS)
                    .stream()
                    .anyMatch(b -> chevauche(b, debut, fin));
            if (enConflit) {
                throw new BusinessRuleException(
                        "Le technicien " + t.getNom() + " a déjà une intervention sur ce créneau."
                );
            }
        }

        // --- 7. Construction du BonTravail ---
        BonTravail bonTravail = new BonTravail();
        bonTravail.setAscenseur(ascenseur);
        bonTravail.setTechnicienResponsable(responsable);
        bonTravail.setTechniciens(equipeComplete);
        bonTravail.setStatut(StatutBonTravail.PLANIFIE);
        bonTravail.setPriorite(priorite);
        bonTravail.setDateInterventionPrevue(debut);
        bonTravail.setDureeEstimeeMinutes(dto.getDureeEstimeeMinutes());
        bonTravail.setDescription(dto.getDescription());
        bonTravail.setCreePar(creePar);

        // --- 8. Renseignement des champs libres (intégration) ---
        bonTravail.setAdresseLibre(dto.getAdresseLibre());
        bonTravail.setVilleLibre(dto.getVilleLibre());
        bonTravail.setNomAscenseurLibre(dto.getNomAscenseurLibre());
        bonTravail.setMessageOriginal(dto.getMessageOriginal());
        bonTravail.setEstUrgence(dto.isEstUrgence());

        // --- 9. Sauvegarde ---
        BonTravail saved = bonTravailRepository.save(bonTravail);

        // --- 10. Notifications ---
        for (Technicien t : equipeComplete) {
            notificationService.creer(
                    t,
                    TypeNotification.NOUVEAU_TRAVAIL_ASSIGNE,
                    "Nouveau travail assigné (via intégration)",
                    "Une intervention " + (dto.isEstUrgence() ? "URGENTE " : "") +
                            "est prévue le " + debut + (ascenseur != null ? " sur " + ascenseur.getNom() : ""),
                    "BON_TRAVAIL",
                    saved.getId()
            );
        }

        return toDTOAvecPhotos(saved);
    }
}