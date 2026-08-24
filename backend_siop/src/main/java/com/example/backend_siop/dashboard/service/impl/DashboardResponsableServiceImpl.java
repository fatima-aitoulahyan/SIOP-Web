package com.example.backend_siop.dashboard.service.impl;

import com.example.backend_siop.dashboard.dto.AnomalieCritiqueDTO;
import com.example.backend_siop.dashboard.dto.DashboardResponsableDTO;
import com.example.backend_siop.maintenance.entity.BonTravail;
import com.example.backend_siop.maintenance.entity.ChecklistMaintenance;
import com.example.backend_siop.maintenance.entity.ItemCheckList;
import com.example.backend_siop.maintenance.enums.GraviteAnomalie;
import com.example.backend_siop.maintenance.enums.PrioriteDemande;
import com.example.backend_siop.maintenance.enums.StatutBonTravail;
import com.example.backend_siop.maintenance.enums.StatutDemande;
import com.example.backend_siop.maintenance.enums.TypeDemande;
import com.example.backend_siop.maintenance.enums.StatutEvaluation;
import com.example.backend_siop.maintenance.repository.BonTravailRepository;
import com.example.backend_siop.maintenance.repository.DemandeMaintenanceRepository;
import com.example.backend_siop.maintenance.repository.ItemCheckListRepository;
import com.example.backend_siop.maintenance.repository.EvaluationAscenseurRepository;
import com.example.backend_siop.dashboard.service.DashboardResponsableService;
import com.example.backend_siop.utilisateur.repository.TechnicienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardResponsableServiceImpl implements DashboardResponsableService {

    private static final int JOURS_ANOMALIES_RECENTES = 7;

    private final DemandeMaintenanceRepository demandeRepository;
    private final BonTravailRepository bonTravailRepository;
    private final ItemCheckListRepository itemCheckListRepository;
    private final TechnicienRepository technicienRepository;
    private final EvaluationAscenseurRepository evaluationRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponsableDTO getStatsResponsable() {
        DashboardResponsableDTO dto = new DashboardResponsableDTO();

        LocalDateTime debutMois = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        // Bilans d'activité (mois en cours)
        dto.setDemandesCeMois(demandeRepository.countByCreatedAtAfter(debutMois));
        dto.setResoluesCeMois(demandeRepository.countByStatutAndDateResolutionAfter(
                                 StatutDemande.RESOLUE, debutMois));

        // Files d'attente (état actuel)
        dto.setEnAttente(demandeRepository.countByStatut(StatutDemande.EN_ATTENTE));
        dto.setAssignees(demandeRepository.countByStatut(StatutDemande.ASSIGNEE));
        dto.setEnCours(demandeRepository.countByStatut(StatutDemande.EN_COURS));
        dto.setUrgentesEnAttente(demandeRepository.countByPrioriteAndStatut(
                PrioriteDemande.URGENTE, StatutDemande.EN_ATTENTE));
        // (compte les rapports d'évaluation envoyés, en attente de décision du Responsable)
        dto.setEvaluationsAValider(evaluationRepository.countByStatut(StatutEvaluation.ENVOYEE));

        // Anomalies critiques récentes
        dto.setNombreAnomaliesCritiques(getAnomaliesCritiques().size());

        // Techniciens
        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour = LocalDate.now().atTime(LocalTime.MAX);

        List<BonTravail> bonsAujourdhui = bonTravailRepository
                .findByDateInterventionPrevueBetweenOrderByDateInterventionPrevueAsc(debutJour, finJour);

        long techniciensEnIntervention = bonsAujourdhui.stream()
                .filter(b -> b.getStatut() == StatutBonTravail.PLANIFIE
                        || b.getStatut() == StatutBonTravail.EN_COURS)
                .flatMap(b -> b.getTechniciens().stream())
                .map(t -> t.getId())
                .distinct()
                .count();

        dto.setTechniciensEnInterventionAujourdhui(techniciensEnIntervention);
        dto.setTechniciensTotal(technicienRepository.count());

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnomalieCritiqueDTO> getAnomaliesCritiques() {
        LocalDateTime depuis = LocalDateTime.now().minusDays(JOURS_ANOMALIES_RECENTES);

        List<ItemCheckList> items = itemCheckListRepository
                .findAnomaliesCritiquesRecentes(GraviteAnomalie.CRITIQUE, depuis);

        return items.stream()
                .map(this::toAnomalieDTO)
                .toList();
    }

    private AnomalieCritiqueDTO toAnomalieDTO(ItemCheckList item) {
        ChecklistMaintenance checklist = item.getChecklistMaintenance();
        BonTravail bonTravail = checklist.getBonTravail();

        Long bonTravailId = bonTravail != null ? bonTravail.getId() : null;
        String ascenseurNom = checklist.getAscenseur() != null
                ? checklist.getAscenseur().getNom() : "—";
        String siteAdresse = (checklist.getAscenseur() != null
                && checklist.getAscenseur().getSiteEntity() != null)
                ? checklist.getAscenseur().getSiteEntity().getAdresse() : "—";

        LocalDate dateCloture = checklist.getUpdatedAt() != null
                ? checklist.getUpdatedAt().toLocalDate() : null;

        return new AnomalieCritiqueDTO(
                bonTravailId,
                ascenseurNom,
                siteAdresse,
                item.getLibelle(),
                item.getRemarque(),
                dateCloture
        );
    }
}
