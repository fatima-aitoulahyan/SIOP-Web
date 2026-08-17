package com.example.backend_siop.maintenance.dto.mapper;

import com.example.backend_siop.maintenance.dto.ChecklistMaintenanceDTO;
import com.example.backend_siop.maintenance.dto.ItemCheckListDTO;
import com.example.backend_siop.maintenance.entity.ChecklistMaintenance;
import com.example.backend_siop.maintenance.entity.ItemCheckList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChecklistMaintenanceMapper {

    @Mapping(target = "ascenseurId", source = "ascenseur.id")
    @Mapping(target = "ascenseurNom", source = "ascenseur.nom")
    @Mapping(target = "bonTravailId", source = "bonTravail.id")
    @Mapping(target = "technicienId", source = "technicien.id")
    @Mapping(target = "technicienNom", source = "technicien.nom")
    @Mapping(target = "items", ignore = true)
    ChecklistMaintenanceDTO toDTO(ChecklistMaintenance entity);

    @Mapping(target = "piecesJointes", ignore = true)
    ItemCheckListDTO toItemDTO(ItemCheckList entity);
}