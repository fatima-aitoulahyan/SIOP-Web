package com.example.backend_siop.utilisateur.dto.mapper;

import com.example.backend_siop.utilisateur.dto.TechnicienResumeDTO;
import com.example.backend_siop.utilisateur.entity.Technicien;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TechnicienMapper {
    TechnicienResumeDTO toResumeDTO(Technicien technicien);
}