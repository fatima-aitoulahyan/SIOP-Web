package com.example.backend_siop.ascenseur.dto.composant.mapper;

import com.example.backend_siop.ascenseur.dto.composant.ComposantCreateDTO;
import com.example.backend_siop.ascenseur.dto.composant.ComposantDTO;
import com.example.backend_siop.ascenseur.dto.composant.ComposantUpdateDTO;
import com.example.backend_siop.ascenseur.entity.Composant;
import com.example.backend_siop.common.util.FileStorageUtil;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ComposantMapper {

    @Autowired
    protected FileStorageUtil fileStorageUtil;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assemblage", ignore = true)
    public abstract Composant toEntity(ComposantCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assemblage", ignore = true)
    public abstract void updateEntityFromDto(ComposantUpdateDTO dto, @MappingTarget Composant composant);

    @Mapping(target = "assemblageId", source = "assemblage.id")
    @Mapping(target = "assemblageNom", source = "assemblage.nom")
    public abstract ComposantDTO toDTO(Composant composant);

    @AfterMapping
    protected void resoudreImageUrl(Composant composant, @MappingTarget ComposantDTO dto) {
        if (composant.getImageUrl() != null) {
            dto.setImageUrl(fileStorageUtil.getUrlTemporaire(composant.getImageUrl()));
        }
    }
}