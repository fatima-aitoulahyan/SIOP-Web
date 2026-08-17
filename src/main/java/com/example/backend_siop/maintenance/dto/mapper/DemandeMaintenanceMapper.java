package com.example.backend_siop.maintenance.dto.mapper;

import com.example.backend_siop.maintenance.dto.DemandeMaintenanceCreateDTO;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceDTO;
import com.example.backend_siop.maintenance.entity.DemandeMaintenance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DemandeMaintenanceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "motifRejet", ignore = true)
    @Mapping(target = "ascenseur", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DemandeMaintenance toEntity(DemandeMaintenanceCreateDTO dto);

    @Mapping(target = "ascenseurId", source = "ascenseur.id")
    @Mapping(target = "ascenseurNom", source = "ascenseur.nom")
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientNom", source = "client.nom")
    @Mapping(target = "villeSaisie", source = "villeSaisie")
    @Mapping(target = "adresseSaisie", source = "adresseSaisie")
    DemandeMaintenanceDTO toDTO(DemandeMaintenance entity);
}