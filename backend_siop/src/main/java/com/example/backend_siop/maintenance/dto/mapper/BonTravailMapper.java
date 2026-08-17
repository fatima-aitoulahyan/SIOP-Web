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

    @Mapping(target = "demandeMaintenanceId", source = "demandeMaintenance.id")
    @Mapping(target = "ascenseurId", source = "ascenseur.id")
    @Mapping(target = "ascenseurNom", source = "ascenseur.nom")
    @Mapping(target = "siteAdresse", source = "ascenseur.siteEntity.adresse")
    @Mapping(target = "parcId", source = "ascenseur.siteEntity.parc.id")
    @Mapping(target = "parcNom", source = "ascenseur.siteEntity.parc.nom")
    @Mapping(target = "technicienResponsableId", source = "technicienResponsable.id")
    @Mapping(target = "technicienResponsableNom", source = "technicienResponsable.nom")
    @Mapping(target = "photosDemande", ignore = true)
    @Mapping(target = "piecesJointesBonTravail", ignore = true)
    public abstract BonTravailDTO toDTO(BonTravail entity);

    @Mapping(target = "ascenseurNom", source = "ascenseur.nom")
    @Mapping(target = "siteAdresse", source = "ascenseur.siteEntity.adresse")
    @Mapping(target = "parcNom", source = "ascenseur.siteEntity.parc.nom")
    @Mapping(target = "technicienResponsableNom", source = "technicienResponsable.nom")
    public abstract BonTravailResumeDTO toResumeDTO(BonTravail entity);

    /**
     * Cas d'une évaluation : bonTravail.ascenseur est null, donc siteAdresse/parcId/parcNom
     * restent vides via le mapping ci-dessus. On les récupère alors via le site
     * rattaché à la demande de maintenance (renseigné lors de l'acceptation).
     */
    @AfterMapping
    protected void enrichirSiteParcDTO(BonTravail entity, @MappingTarget BonTravailDTO dto) {
        if (entity.getAscenseur() != null) return; // déjà rempli via l'ascenseur

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