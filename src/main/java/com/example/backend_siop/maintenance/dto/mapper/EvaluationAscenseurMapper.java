package com.example.backend_siop.maintenance.dto.mapper;

import com.example.backend_siop.maintenance.dto.EvaluationAscenseurDTO;
import com.example.backend_siop.maintenance.entity.EvaluationAscenseur;
import org.springframework.stereotype.Component;

@Component
public class EvaluationAscenseurMapper {

    public EvaluationAscenseurDTO toDTO(EvaluationAscenseur e) {
        return EvaluationAscenseurDTO.builder()
                .id(e.getId())
                .bonTravailId(e.getBonTravail() != null ? e.getBonTravail().getId() : null)
                .technicienId(e.getTechnicien() != null ? e.getTechnicien().getId() : null)
                .technicienNom(e.getTechnicien() != null ? e.getTechnicien().getNom() : null)
                .dateVisite(e.getDateVisite())
                .nom(e.getNom())
                .fabricant(e.getFabricant())
                .marque(e.getMarque())
                .modele(e.getModele())
                .numeroSerie(e.getNumeroSerie())
                .codeBarre(e.getCodeBarre())
                .nombreEtages(e.getNombreEtages())
                .capacitePersonnes(e.getCapacitePersonnes())
                .chargeMaxKg(e.getChargeMaxKg())
                .vitesse(e.getVitesse())
                .puissance(e.getPuissance())
                .type(e.getType())
                .dateMiseEnService(e.getDateMiseEnService())
                .etatPortes(e.getEtatPortes())
                .positionCabine(e.getPositionCabine())
                .anomalies(e.getAnomalies())
                .causeExterieure(e.getCauseExterieure())
                .observations(e.getObservations())
                .statut(e.getStatut())
                .motifRefus(e.getMotifRefus())
                .responsableId(e.getResponsable() != null ? e.getResponsable().getId() : null)
                .dateDecision(e.getDateDecision())
                .ascenseurCreeId(e.getAscenseurCree() != null ? e.getAscenseurCree().getId() : null)
                .build();
    }
}