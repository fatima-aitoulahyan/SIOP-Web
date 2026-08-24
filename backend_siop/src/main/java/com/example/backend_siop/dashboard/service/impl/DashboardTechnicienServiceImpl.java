package com.example.backend_siop.dashboard.service.impl;

import com.example.backend_siop.maintenance.dto.BonTravailResumeDTO;
import com.example.backend_siop.dashboard.dto.DashboardTechnicienDTO;
import com.example.backend_siop.dashboard.dto.PlanningJourDTO;
import com.example.backend_siop.dashboard.dto.ProchaineInterventionDTO;
import com.example.backend_siop.maintenance.dto.mapper.BonTravailMapper;
import com.example.backend_siop.maintenance.entity.BonTravail;
import com.example.backend_siop.maintenance.enums.StatutBonTravail;
import com.example.backend_siop.maintenance.repository.BonTravailRepository;
import com.example.backend_siop.dashboard.service.DashboardTechnicienService;
import com.example.backend_siop.utilisateur.entity.Technicien;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DashboardTechnicienServiceImpl implements DashboardTechnicienService {

    private final BonTravailRepository bonTravailRepository;
    private final BonTravailMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public DashboardTechnicienDTO getStats(Technicien technicien) {
        DashboardTechnicienDTO dto = new DashboardTechnicienDTO();

        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime debutMois = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMois = LocalDate.now()
                 .withDayOfMonth(LocalDate.now().lengthOfMonth())
                 .atTime(LocalTime.MAX);


        // Aujourd'hui : interventions prévues aujourd'hui
        dto.setInterventionsAujourdhui(
        bonTravailRepository.findByTechniciensContainingAndDateInterventionPrevueBetween(
                technicien, debutJour, finJour)
                .stream()
                .filter(b -> b.getStatut() != StatutBonTravail.ANNULE)
                .count());

        // En cours
        dto.setEnCours(bonTravailRepository.countByTechniciensContainingAndStatut(
                technicien, StatutBonTravail.EN_COURS));

        // Total ce mois
        List<BonTravail> interventionsMois = bonTravailRepository
        .findByTechniciensContainingAndDateInterventionPrevueBetween(
                technicien, debutMois, finMois);
        dto.setTotalCeMois(
              interventionsMois.stream()
                .filter(b -> b.getStatut() != StatutBonTravail.ANNULE)  // P2 : exclure annulés
                .count());

        // Terminées ce mois
        dto.setTermineesCeMois(
                bonTravailRepository.countByTechniciensContainingAndStatutAndDateFinReelleAfter(
                        technicien, StatutBonTravail.TERMINE, debutMois));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BonTravailResumeDTO> getInterventionsAujourdhui(Technicien technicien) {
        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour = LocalDate.now().atTime(LocalTime.MAX);

        return bonTravailRepository
                .findByTechniciensContainingAndDateInterventionPrevueBetween(
                        technicien, debutJour, finJour)
                .stream()
                .filter(b -> b.getStatut() != StatutBonTravail.ANNULE)
                .sorted((a, b) -> a.getDateInterventionPrevue().compareTo(b.getDateInterventionPrevue()))
                .map(mapper::toResumeDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProchaineInterventionDTO getProchaineIntervention(Technicien technicien) {
        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour = LocalDate.now().atTime(LocalTime.MAX);

        // 1. Chercher une intervention non démarrée aujourd'hui (PLANIFIE), la plus proche
        List<BonTravail> aujourdhui = bonTravailRepository
                .findByTechniciensContainingAndDateInterventionPrevueBetween(
                        technicien, debutJour, finJour)
                .stream()
                .filter(b -> b.getStatut() == StatutBonTravail.PLANIFIE)
                .sorted((a, b) -> a.getDateInterventionPrevue().compareTo(b.getDateInterventionPrevue()))
                .toList();

        if (!aujourdhui.isEmpty()) {
            BonTravail prochaine = aujourdhui.get(0);
            boolean enRetard = prochaine.getDateInterventionPrevue().isBefore(LocalDateTime.now());
            return new ProchaineInterventionDTO(
                    mapper.toResumeDTO(prochaine), "AUJOURDHUI", enRetard);
        }

        // 2. Sinon, chercher la première intervention de demain
        LocalDateTime debutDemain = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime finDemain = LocalDate.now().plusDays(1).atTime(LocalTime.MAX);

        List<BonTravail> demain = bonTravailRepository
                .findByTechniciensContainingAndDateInterventionPrevueBetween(
                        technicien, debutDemain, finDemain)
                .stream()
                .filter(b -> b.getStatut() == StatutBonTravail.PLANIFIE)
                .sorted((a, b) -> a.getDateInterventionPrevue().compareTo(b.getDateInterventionPrevue()))
                .toList();

        if (!demain.isEmpty()) {
            return new ProchaineInterventionDTO(
                    mapper.toResumeDTO(demain.get(0)), "DEMAIN", false);
        }

        // 3. Rien aujourd'hui ni demain
        return new ProchaineInterventionDTO(null, "AUCUNE", false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanningJourDTO> getPlanningSemaine(Technicien technicien) {
        // Début de la semaine (lundi)
        LocalDate aujourdhui = LocalDate.now();
        LocalDate lundi = aujourdhui.with(DayOfWeek.MONDAY);

        return java.util.stream.IntStream.range(0, 7)
                .mapToObj(i -> {
                    LocalDate jour = lundi.plusDays(i);
                    LocalDateTime debut = jour.atStartOfDay();
                    LocalDateTime fin = jour.atTime(LocalTime.MAX);

                    long nombre = bonTravailRepository
                       .findByTechniciensContainingAndDateInterventionPrevueBetween(
                        technicien, debut, fin)
                        .stream()
                        .filter(b -> b.getStatut() != StatutBonTravail.ANNULE)  // ← AJOUTER
                        .count();

                    String label = jour.getDayOfWeek()
                            .getDisplayName(TextStyle.SHORT, Locale.FRENCH);

                    return new PlanningJourDTO(jour, label, nombre);
                })
                .toList();
    }
}