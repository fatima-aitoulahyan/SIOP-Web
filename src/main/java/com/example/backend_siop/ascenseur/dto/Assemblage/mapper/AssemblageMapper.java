package com.example.backend_siop.ascenseur.dto.Assemblage.mapper;

import com.example.backend_siop.ascenseur.dto.Assemblage.AssemblageDTO;
import com.example.backend_siop.ascenseur.dto.Assemblage.AssemblageTreeDTO;
import com.example.backend_siop.ascenseur.dto.composant.mapper.ComposantMapper;
import com.example.backend_siop.ascenseur.entity.Assemblage;
import com.example.backend_siop.common.util.FileStorageUtil;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class AssemblageMapper {

    @Autowired
    protected FileStorageUtil fileStorageUtil;

    @Mapping(target = "ascenseurId", source = "ascenseur.id")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "parentNom", source = "parent.nom")
    public abstract AssemblageDTO toDTO(Assemblage assemblage);

    public abstract List<AssemblageDTO> toDTOList(List<Assemblage> assemblages);

    @AfterMapping
    protected void resoudreImageUrl(Assemblage assemblage, @MappingTarget AssemblageDTO dto) {
        if (assemblage.getImageUrl() != null) {
            dto.setImageUrl(fileStorageUtil.getUrlTemporaire(assemblage.getImageUrl()));
        }
    }

    public AssemblageTreeDTO toTreeDTO(Assemblage assemblage, ComposantMapper composantMapper) {
        if (assemblage == null) return null;

        AssemblageTreeDTO dto = new AssemblageTreeDTO();
        dto.setId(assemblage.getId());
        dto.setNom(assemblage.getNom());
        dto.setReference(assemblage.getReference());       // ← ajouté
        dto.setType(assemblage.getType());                   // ← ajouté
        dto.setDescription(assemblage.getDescription());
        dto.setNiveau(assemblage.getNiveau());
        dto.setImageUrl(assemblage.getImageUrl() != null
                ? fileStorageUtil.getUrlTemporaire(assemblage.getImageUrl())
                : null);
        dto.setSousAssemblages(
                assemblage.getSousAssemblages().stream()
                        .map(sous -> toTreeDTO(sous, composantMapper))
                        .toList()
        );
        dto.setComposants(
                assemblage.getComposants().stream()
                        .map(composantMapper::toDTO)
                        .toList()
        );
        return dto;
    }
    public List<AssemblageTreeDTO> toTreeDTOList(List<Assemblage> assemblages, ComposantMapper composantMapper) {
        return assemblages.stream().map(a -> toTreeDTO(a, composantMapper)).toList();
    }
}