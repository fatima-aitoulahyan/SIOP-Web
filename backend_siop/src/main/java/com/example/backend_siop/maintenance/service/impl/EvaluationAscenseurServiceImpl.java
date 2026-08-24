package com.example.backend_siop.maintenance.service.impl;

import com.example.backend_siop.ascenseur.entity.Ascenseur;
import com.example.backend_siop.ascenseur.repository.AscenseurRepository;
import com.example.backend_siop.maintenance.dto.EvaluationAscenseurDTO;
import com.example.backend_siop.maintenance.dto.EvaluationAscenseurSoumissionDto;
import com.example.backend_siop.maintenance.dto.EvaluationAscenseurValidationDto;
import com.example.backend_siop.maintenance.dto.mapper.EvaluationAscenseurMapper;
import com.example.backend_siop.maintenance.entity.BonTravail;
import com.example.backend_siop.maintenance.entity.DemandeMaintenance;
import com.example.backend_siop.maintenance.entity.EvaluationAscenseur;
import com.example.backend_siop.maintenance.enums.StatutBonTravail;
import com.example.backend_siop.maintenance.enums.StatutDemande;
import com.example.backend_siop.maintenance.enums.StatutEvaluation;
import com.example.backend_siop.maintenance.repository.BonTravailRepository;
import com.example.backend_siop.maintenance.repository.EvaluationAscenseurRepository;
import com.example.backend_siop.maintenance.service.EvaluationAscenseurService;
import com.example.backend_siop.common.exception.BusinessRuleException;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import com.example.backend_siop.utilisateur.repository.TechnicienRepository;
import com.example.backend_siop.utilisateur.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationAscenseurServiceImpl implements EvaluationAscenseurService {

    private final EvaluationAscenseurRepository evaluationRepository;
    private final AscenseurRepository ascenseurRepository;
    private final BonTravailRepository bonTravailRepository;
    private final TechnicienRepository technicienRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EvaluationAscenseurMapper mapper;

    @Override
    @Transactional
    public EvaluationAscenseurDTO creerBrouillon(Long bonTravailId, Long technicienId) {
        BonTravail bonTravail = bonTravailRepository.findById(bonTravailId)
                .orElseThrow(() -> new ResourceNotFoundException("BonTravail introuvable"));

        if (evaluationRepository.findByBonTravail_Id(bonTravailId).isPresent()) {
            throw new BusinessRuleException("Une évaluation existe déjà pour ce bon de travail.");
        }

        Technicien technicien = technicienRepository.findById(technicienId)
                .orElseThrow(() -> new ResourceNotFoundException("Technicien introuvable"));

        EvaluationAscenseur evaluation = new EvaluationAscenseur();
        evaluation.setBonTravail(bonTravail);
        evaluation.setTechnicien(technicien);
        evaluation.setStatut(StatutEvaluation.BROUILLON);

        EvaluationAscenseur saved = evaluationRepository.save(evaluation);
        return mapper.toDTO(saved);
    }

    @Override
    @Transactional
    public EvaluationAscenseurDTO soumettre(Long evaluationId, EvaluationAscenseurSoumissionDto dto, Long technicienId) {
        EvaluationAscenseur evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("Évaluation introuvable"));

        if (!evaluation.getTechnicien().getId().equals(technicienId)) {
            throw new AccessDeniedException("Vous n'êtes pas le technicien assigné à cette évaluation.");
        }

        if (evaluation.getStatut() != StatutEvaluation.BROUILLON) {
            throw new BusinessRuleException("Seule une évaluation BROUILLON peut être soumise.");
        }

        if (dto.getNumeroSerie() != null && !dto.getNumeroSerie().isBlank()
                && ascenseurRepository.existsByNumeroSerie(dto.getNumeroSerie())) {
            throw new BusinessRuleException("Un ascenseur avec ce numéro de série existe déjà.");
        }

        evaluation.setFabricant(dto.getFabricant());
        evaluation.setMarque(dto.getMarque());
        evaluation.setModele(dto.getModele());
        evaluation.setNumeroSerie(dto.getNumeroSerie());
        evaluation.setCodeBarre(dto.getCodeBarre());
        evaluation.setNombreEtages(dto.getNombreEtages());
        evaluation.setCapacitePersonnes(dto.getCapacitePersonnes());
        evaluation.setChargeMaxKg(dto.getChargeMaxKg());
        evaluation.setVitesse(dto.getVitesse());
        evaluation.setPuissance(dto.getPuissance());
        evaluation.setType(dto.getType());
        evaluation.setDateMiseEnService(dto.getDateMiseEnService());

        evaluation.setEtatPortes(dto.getEtatPortes());
        evaluation.setPositionCabine(dto.getPositionCabine());
        evaluation.setAnomalies(dto.getAnomalies());
        evaluation.setCauseExterieure(dto.getCauseExterieure());
        evaluation.setObservations(dto.getObservations());

        evaluation.setDateVisite(LocalDateTime.now());
        evaluation.setStatut(StatutEvaluation.ENVOYEE);

        EvaluationAscenseur saved = evaluationRepository.save(evaluation);
        return mapper.toDTO(saved);
    }

    @Override
    @Transactional
    public EvaluationAscenseurDTO valider(Long evaluationId, Long responsableId, EvaluationAscenseurValidationDto dto) {
        EvaluationAscenseur evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("Évaluation introuvable"));

        if (evaluation.getStatut() != StatutEvaluation.ENVOYEE) {
            throw new BusinessRuleException("Seule une évaluation ENVOYEE peut être validée.");
        }

        Utilisateur responsable = utilisateurRepository.findById(responsableId)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable introuvable"));

        evaluation.setResponsable(responsable);
        evaluation.setDateDecision(LocalDateTime.now());

        if (dto.isAccepter()) {
            accepterEvaluation(evaluation);
        } else {
            refuserEvaluation(evaluation, dto.getMotif()); // Appel de la méthode de refus
        }

        EvaluationAscenseur saved = evaluationRepository.save(evaluation);
        return mapper.toDTO(saved);
    }
    private void refuserEvaluation(EvaluationAscenseur evaluation, String motif) {
        evaluation.setStatut(StatutEvaluation.REFUSEE);
        evaluation.setMotifRefus(motif);

        BonTravail bonTravail = evaluation.getBonTravail();
        if (bonTravail != null) {
            bonTravail.setStatut(StatutBonTravail.TERMINE);
            bonTravail.setDateFinReelle(LocalDateTime.now());
            bonTravailRepository.save(bonTravail);

            DemandeMaintenance demande = bonTravail.getDemandeMaintenance();
            if (demande != null) {
                demande.setStatut(StatutDemande.REJETEE);
                demande.setDateResolution(LocalDateTime.now());
            }
        }
    }

    private void accepterEvaluation(EvaluationAscenseur evaluation) {
        if (evaluation.getNumeroSerie() != null && !evaluation.getNumeroSerie().isBlank()
                && ascenseurRepository.existsByNumeroSerie(evaluation.getNumeroSerie())) {
            throw new BusinessRuleException("Un ascenseur avec ce numéro de série existe déjà.");
        }

        BonTravail bonTravail = evaluation.getBonTravail();
        DemandeMaintenance demande = bonTravail.getDemandeMaintenance();

        if (demande == null || demande.getClient() == null || demande.getSite() == null) {
            throw new BusinessRuleException("Client/Site introuvable pour créer l'ascenseur.");
        }

        Ascenseur ascenseur = new Ascenseur();
        ascenseur.setNom(construireNom(evaluation, demande));
        ascenseur.setFabricant(evaluation.getFabricant());
        ascenseur.setMarque(evaluation.getMarque());
        ascenseur.setModele(evaluation.getModele());
        ascenseur.setNumeroSerie(evaluation.getNumeroSerie());
        ascenseur.setCodeBarre(evaluation.getCodeBarre());
        ascenseur.setNombreEtages(evaluation.getNombreEtages());
        ascenseur.setCapacitePersonnes(evaluation.getCapacitePersonnes());
        ascenseur.setChargeMaxKg(evaluation.getChargeMaxKg());
        ascenseur.setVitesse(evaluation.getVitesse());
        ascenseur.setPuissance(evaluation.getPuissance());
        ascenseur.setType(evaluation.getType());
        ascenseur.setDateMiseEnService(
                evaluation.getDateMiseEnService() != null
                        ? evaluation.getDateMiseEnService()
                        : LocalDate.now());
        ascenseur.setClient(demande.getClient());
        ascenseur.setSiteEntity(demande.getSite());
        ascenseur.setActif(true);

        Ascenseur saved = ascenseurRepository.save(ascenseur);

        evaluation.setAscenseurCree(saved);
        evaluation.setStatut(StatutEvaluation.ACCEPTEE);

        bonTravail.setAscenseur(saved);
        bonTravail.setStatut(StatutBonTravail.TERMINE);
        demande.setStatut(StatutDemande.RESOLUE);
        demande.setDateResolution(LocalDateTime.now());
        bonTravail.setDateFinReelle(LocalDateTime.now());
        bonTravailRepository.save(bonTravail);
    }

    private String construireNom(EvaluationAscenseur evaluation, DemandeMaintenance demande) {
        return "%s %s - %s".formatted(
                evaluation.getFabricant() != null ? evaluation.getFabricant() : "Ascenseur",
                evaluation.getModele() != null ? evaluation.getModele() : "",
                demande.getClient().getNom()
        ).trim();
    }

    @Override
    public EvaluationAscenseurDTO getById(Long evaluationId) {
        EvaluationAscenseur evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("Évaluation introuvable"));
        return mapper.toDTO(evaluation);
    }

    @Override
    public List<EvaluationAscenseurDTO> getEnAttenteValidation() {
        return evaluationRepository.findByStatut(StatutEvaluation.ENVOYEE).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public EvaluationAscenseurDTO getByBonTravailId(Long bonTravailId) {
        EvaluationAscenseur evaluation = evaluationRepository.findByBonTravail_Id(bonTravailId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune évaluation pour ce bon de travail"));
        return mapper.toDTO(evaluation);
    }

    @Override
    public List<EvaluationAscenseurDTO> listerParTechnicien(Long technicienId) {
        return evaluationRepository.findByTechnicien_Id(technicienId).stream()
                .map(mapper::toDTO)
                .toList();
    }
    @Override
    public List<EvaluationAscenseurDTO> listerParClient(Long clientId) {
        return evaluationRepository.findByBonTravail_DemandeMaintenance_Client_Id(clientId).stream()
                .map(mapper::toDTO)
                .toList();
    }
}