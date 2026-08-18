package com.example.backend_siop.ascenseur.dto.mapperAscenseur;

import com.example.backend_siop.ascenseur.dto.Ascenseur.AscenseurDTO;
import com.example.backend_siop.ascenseur.entity.Ascenseur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AscenseurMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientPrenom", source = "client.prenom")
    @Mapping(target = "clientNom", source = "client.nom")
    @Mapping(target = "clientNomEntreprise", source = "client.nomEntreprise")
    @Mapping(target = "siteId", source = "siteEntity.id")
    @Mapping(target = "siteAdresse", source = "siteEntity.adresse")
    @Mapping(target = "parcId", source = "siteEntity.parc.id")
    @Mapping(target = "parcNom", source = "siteEntity.parc.nom")
    @Mapping(target = "marque", source = "marque")
    @Mapping(target = "piecesJointes", ignore = true)
    AscenseurDTO toDTO(Ascenseur ascenseur);

    List<AscenseurDTO> toDTOList(List<Ascenseur> ascenseurs);
}