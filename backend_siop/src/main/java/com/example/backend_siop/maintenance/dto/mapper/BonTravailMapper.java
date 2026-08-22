package com.example.backend_siop.maintenance.dto.mapper;

import com.example.backend_siop.ascenseur.entity.Site;
import com.example.backend_siop.maintenance.dto.BonTravailDTO;
import com.example.backend_siop.maintenance.dto.BonTravailResumeDTO;
import com.example.backend_siop.maintenance.entity.BonTravail;
import com.example.backend_siop.utilisateur.dto.mapper.TechnicienMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = TechnicienMapper.class)
public abstract class BonTravailMapper {

    // =========================================================
    // Mapping pour le DTO complet (Détail)
    // =========================================================
    @Mapping(target = "demandeMaintenanceId", source = "demandeMaintenance.id")
    @Mapping(target = "ascenseurId", source = "ascenseur.id")
    @Mapping(target = "ascenseurNom", source = "ascenseur.nom")
    @Mapping(target = "siteAdresse", source = "ascenseur.siteEntity.adresse")
    @Mapping(target = "parcId", source = "ascenseur.siteEntity.parc.id")
    @Mapping(target = "parcNom", source = "ascenseur.siteEntity.parc.nom")
    @Mapping(target = "technicienResponsableId", source = "technicienResponsable.id")
    @Mapping(target = "technicienResponsableNom", source = "technicienResponsable.nom")

    @Mapping(target = "dureeEstimeeMinutes", source = "dureeEstimeeMinutes")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "priorite", source = "priorite")
    @Mapping(target = "statut", source = "statut")
    @Mapping(target = "dateInterventionPrevue", source = "dateInterventionPrevue")
    @Mapping(target = "dateDebutReelle", source = "dateDebutReelle")
    @Mapping(target = "dateFinReelle", source = "dateFinReelle")
    @Mapping(target = "diagnostic", source = "diagnostic")
    @Mapping(target = "causeIdentifiee", source = "causeIdentifiee")
    @Mapping(target = "actionRealisee", source = "actionRealisee")
    @Mapping(target = "piecesRemplacees", source = "piecesRemplacees")
    @Mapping(target = "essaiConcluant", source = "essaiConcluant")
    @Mapping(target = "recommandations", source = "recommandations")
    @Mapping(target = "createdAt", source = "createdAt")

    @Mapping(target = "photosDemande", ignore = true)
    @Mapping(target = "piecesJointesBonTravail", ignore = true)
    public abstract BonTravailDTO toDTO(BonTravail entity);

    // =========================================================
    // Mapping pour le DTO Résumé (Liste)
    // =========================================================
    @Mapping(target = "ascenseurNom", source = "ascenseur.nom")
    @Mapping(target = "siteAdresse", source = "ascenseur.siteEntity.adresse")
    @Mapping(target = "parcNom", source = "ascenseur.siteEntity.parc.nom")
    @Mapping(target = "technicienResponsableNom", source = "technicienResponsable.nom")

    @Mapping(target = "priorite", source = "priorite")
    @Mapping(target = "statut", source = "statut")
    @Mapping(target = "dateInterventionPrevue", source = "dateInterventionPrevue")

    public abstract BonTravailResumeDTO toResumeDTO(BonTravail entity);

    // =========================================================
    // AfterMapping pour gérer le cas des évaluations (sans ascenseur)
    // =========================================================
    @AfterMapping
    protected void enrichirSiteParcDTO(BonTravail entity, @MappingTarget BonTravailDTO dto) {
        if (entity.getAscenseur() != null) return;

        Site site = entity.getDemandeMaintenance() != null
                ? entity.getDemandeMaintenance().getSite()
                : null;
        if (site == null) return;

        dto.setSiteAdresse(site.getAdresse());
        if (site.getParc() != null) {
            dto.setParcId(site.getParc().getId());
            dto.setParcNom(site.getParc().getNom());
        }
    }

    @AfterMapping
    protected void enrichirSiteParcResumeDTO(BonTravail entity, @MappingTarget BonTravailResumeDTO dto) {
        if (entity.getAscenseur() != null) return;

        Site site = entity.getDemandeMaintenance() != null
                ? entity.getDemandeMaintenance().getSite()
                : null;
        if (site == null) return;

        dto.setSiteAdresse(site.getAdresse());
        if (site.getParc() != null) {
            dto.setParcNom(site.getParc().getNom());
        }
    }
}