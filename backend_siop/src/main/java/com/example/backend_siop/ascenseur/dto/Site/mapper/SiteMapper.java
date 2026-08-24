package com.example.backend_siop.ascenseur.dto.Site.mapper;

import com.example.backend_siop.ascenseur.dto.Site.SiteDTO;
import com.example.backend_siop.ascenseur.entity.Site;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SiteMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientNom", source = "client.nom")
    @Mapping(target = "clientPrenom", source = "client.prenom")
    @Mapping(target = "clientEmail", source = "client.email")
    @Mapping(target = "villeId", source = "ville.id")
    @Mapping(target = "villeNom", source = "ville.nom")
    @Mapping(target = "villeCodePostal", source = "ville.codePostal")
    @Mapping(target = "villeRegion", source = "ville.region")
    @Mapping(target = "parcId", source = "parc.id")
    @Mapping(target = "parcNom", source = "parc.nom")
    SiteDTO toDTO(Site site);
}