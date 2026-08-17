package com.example.backend_siop.parc.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.backend_siop.parc.dto.ParcCreateDTO;
import com.example.backend_siop.parc.dto.ParcDTO;
import com.example.backend_siop.parc.entity.Parc;

@Mapper(componentModel = "spring")
public interface ParcMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Parc toEntity(ParcCreateDTO dto);

    @Mapping(target = "nombreSites", ignore = true)
    @Mapping(target = "nombreTechniciens", ignore = true)
    ParcDTO toDTO(Parc entity);
}