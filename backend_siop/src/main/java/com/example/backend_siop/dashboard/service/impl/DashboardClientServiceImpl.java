package com.example.backend_siop.dashboard.service.impl;

import com.example.backend_siop.ascenseur.entity.Ascenseur;
import com.example.backend_siop.ascenseur.repository.AscenseurRepository;
import com.example.backend_siop.dashboard.dto.AscenseurAvecEtatDTO;
import com.example.backend_siop.dashboard.dto.DashboardClientDTO;
import com.example.backend_siop.dashboard.dto.DemandeSuiviDTO;
import com.example.backend_siop.maintenance.entity.BonTravail;
import com.example.backend_siop.maintenance.entity.DemandeMaintenance;
import com.example.backend_siop.maintenance.enums.StatutBonTravail;
import com.example.backend_siop.maintenance.enums.StatutDemande;
import com.example.backend_siop.maintenance.enums.TypeDemande;
import com.example.backend_siop.maintenance.repository.BonTravailRepository;
import com.example.backend_siop.maintenance.repository.DemandeMaintenanceRepository;
import com.example.backend_siop.dashboard.service.DashboardClientService;
import com.example.backend_siop.utilisateur.entity.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardClientServiceImpl implements DashboardClientService {

    private final DemandeMaintenanceRepository demandeRepository;
    private final AscenseurRepository ascenseurRepository;
    private final BonTravailRepository bonTravailRepository;

    // Les statuts considérés comme "demande active"
    private static final Set<StatutDemande> STATUTS_ACTIFS = EnumSet.of(
            StatutDemande.EN_ATTENTE,
            StatutDemande.ASSIGNEE,
            StatutDemande.EN_COURS);

    @Override
    @Transactional(readOnly = true)
    public DashboardClientDTO getStats(Client client) {
        DashboardClientDTO dto = new DashboardClientDTO();

        // Ascenseurs du client
        List<Ascenseur> ascenseurs = ascenseurRepository.findByClientId(client.getId());
        dto.setNombreAscenseurs(ascenseurs.size());

        // Toutes les demandes du client (une seule requête, réutilisée pour tous les compteurs)
        List<DemandeMaintenance> demandes = demandeRepository
                .findByClientIdOrderByCreatedAtDesc(client.getId());

        dto.setDemandesTotal(demandes.size());
        dto.setAssignees(compter(demandes, StatutDemande.ASSIGNEE));
        dto.setEnCours(compter(demandes, StatutDemande.EN_COURS));
        dto.setResolues(compter(demandes, StatutDemande.RESOLUE));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AscenseurAvecEtatDTO> getAscenseursAvecEtat(Client client) {
        List<Ascenseur> ascenseurs = ascenseurRepository.findByClientId(client.getId());
        List<DemandeMaintenance> demandes = demandeRepository
                .findByClientIdOrderByCreatedAtDesc(client.getId());

        // Construire une map : ascenseurId -> statut de sa demande active (s'il y en a une)
        Map<Long, StatutDemande> etatParAscenseur = new HashMap<>();
        for (DemandeMaintenance demande : demandes) {
            if (demande.getAscenseur() != null
                    && STATUTS_ACTIFS.contains(demande.getStatut())) {
                Long ascenseurId = demande.getAscenseur().getId();
                // Si plusieurs demandes actives, on garde la première rencontrée
                // (les demandes sont triées par date décroissante, donc la plus récente)
                etatParAscenseur.putIfAbsent(ascenseurId, demande.getStatut());
            }
        }

        // Construire la liste enrichie
        return ascenseurs.stream()
                .map(a -> {
                    StatutDemande statut = etatParAscenseur.get(a.getId());
                    String siteAdresse = a.getSiteEntity() != null
                            ? a.getSiteEntity().getAdresse() : "—";
                    return new AscenseurAvecEtatDTO(
                            a.getId(),
                            a.getNom(),
                            siteAdresse,
                            statut != null ? statut.name() : null,
                            statut != null
                    );
                })
                .toList();
    }

    private long compter(List<DemandeMaintenance> demandes, StatutDemande statut) {
        return demandes.stream()
                .filter(d -> d.getStatut() == statut)
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandeSuiviDTO> getSuiviDemandes(Client client) {
        List<DemandeMaintenance> demandes = demandeRepository
            .findByClientIdOrderByCreatedAtDesc(client.getId());

        // Ne garder que les demandes actives (en attente, assignées, en cours)
        return demandes.stream()
            .filter(d -> STATUTS_ACTIFS.contains(d.getStatut()))
            .map(this::toSuiviDTO)
            .toList();
}

    private DemandeSuiviDTO toSuiviDTO(DemandeMaintenance demande) {
         // Nom de l'ascenseur (ou texte libre pour une évaluation sans ascenseur)
        String ascenseurNom;
        if (demande.getAscenseur() != null) {
            ascenseurNom = demande.getAscenseur().getNom();
        } else if (demande.getTypeDemande() == TypeDemande.EVALUATION) {
            ascenseurNom = "Évaluation · " + (demande.getAdresseSaisie() != null
                    ? demande.getAdresseSaisie() : "nouvelle installation");
        } else {
             ascenseurNom = "—";
        }

        // Nom du technicien : seulement si un bon de travail existe
        String technicienNom = null;
        Optional<BonTravail> bonTravail = bonTravailRepository
                .findByDemandeMaintenanceId(demande.getId());
        if (bonTravail.isPresent() && bonTravail.get().getStatut() != StatutBonTravail.ANNULE && bonTravail.get().getTechnicienResponsable() != null) {
            var tech = bonTravail.get().getTechnicienResponsable();
            technicienNom = tech.getPrenom() + " " + tech.getNom();
       }

        return new DemandeSuiviDTO(
                demande.getId(),
                ascenseurNom,
                demande.getTypeDemande().name(),
                demande.getStatut().name(),
                technicienNom,
                demande.getCreatedAt() != null ? demande.getCreatedAt().toLocalDate() : null
    );
    }
}
