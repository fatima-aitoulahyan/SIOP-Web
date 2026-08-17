package com.example.backend_siop.maintenance.service.impl;

import com.example.backend_siop.maintenance.dto.CalendrierEventDTO;
import com.example.backend_siop.maintenance.entity.BonTravail;
import com.example.backend_siop.maintenance.entity.Evenement;
import com.example.backend_siop.maintenance.repository.BonTravailRepository;
import com.example.backend_siop.maintenance.repository.EvenementRepository;
import com.example.backend_siop.maintenance.service.CalendrierService;
import com.example.backend_siop.utilisateur.entity.Technicien;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendrierServiceImpl implements CalendrierService {

    private final BonTravailRepository bonTravailRepository;
    private final EvenementRepository evenementRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CalendrierEventDTO> getEvenementsCalendrier(
            LocalDateTime debut, LocalDateTime fin, Long technicienId) {

        List<CalendrierEventDTO> resultat = new ArrayList<>();

        List<BonTravail> bonsTravail =
                bonTravailRepository.findBonsTravailDansPeriode(debut, fin, technicienId);

        for (BonTravail bt : bonsTravail) {
            List<Technicien> techniciens = new ArrayList<>(bt.getTechniciens());
            if (!techniciens.contains(bt.getTechnicienResponsable())) {
                techniciens.add(0, bt.getTechnicienResponsable());
            }

            resultat.add(CalendrierEventDTO.builder()
                    .id("BT-" + bt.getId())
                    .titre(bt.getAscenseur() != null
                            ? "BT #" + bt.getId() + " - " + bt.getAscenseur().getNom()
                            : "BT #" + bt.getId())
                    .source("BON_TRAVAIL")
                    .type(bt.getStatut().name())
                    .debut(bt.getDateInterventionPrevue())
                    .fin(bt.getDateInterventionPrevue()
                            .plusMinutes(bt.getDureeEstimeeMinutes()))
                    .lieu(bt.getAscenseur() != null && bt.getAscenseur().getSiteEntity() != null
                            ? bt.getAscenseur().getSiteEntity().getAdresse() : null)
                    .technicienIds(techniciens.stream().map(Technicien::getId).toList())
                    .technicienNoms(techniciens.stream()
                            .map(t -> t.getNom() + " " + t.getPrenom())
                            .collect(Collectors.toList()))
                    .couleur(couleurSelonStatutBonTravail(bt.getStatut().name()))
                    .build());
        }

        List<Evenement> evenements =
                evenementRepository.findEvenementsDansPeriode(debut, fin, technicienId);

        for (Evenement e : evenements) {
            resultat.add(CalendrierEventDTO.builder()
                    .id("EVT-" + e.getId())
                    .titre(e.getTitre())
                    .source("EVENEMENT")
                    .type(e.getType().name())
                    .debut(e.getDateDebut())
                    .fin(e.getDateFin())
                    .lieu(e.getLieu())
                    .technicienIds(e.getParticipants().stream().map(Technicien::getId).toList())
                    .technicienNoms(e.getParticipants().stream()
                            .map(t -> t.getNom() + " " + t.getPrenom())
                            .collect(Collectors.toList()))
                    .couleur(couleurSelonTypeEvenement(e.getType().name()))
                    .build());
        }

        resultat.sort((a, b) -> a.getDebut().compareTo(b.getDebut()));
        return resultat;
    }

    private String couleurSelonStatutBonTravail(String statut) {
        return switch (statut) {
            case "EN_COURS" -> "#3b82f6";
            case "TERMINE" -> "#22c55e";
            case "ANNULE" -> "#9ca3af";
            case "PLANIFIE" -> "#f59e0b";
            default -> "#6366f1";
        };
    }

    private String couleurSelonTypeEvenement(String type) {
        return switch (type) {
            case "REUNION" -> "#a855f7";
            case "CONGE" -> "#ef4444";
            case "FORMATION" -> "#14b8a6";
            default -> "#64748b";
        };
    }
}