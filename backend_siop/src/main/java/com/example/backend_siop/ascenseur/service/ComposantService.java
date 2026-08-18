package com.example.backend_siop.ascenseur.service;

import com.example.backend_siop.ascenseur.dto.composant.ComposantCreateDTO;
import com.example.backend_siop.ascenseur.dto.composant.ComposantDTO;
import com.example.backend_siop.ascenseur.dto.composant.ComposantUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ComposantService {
    ComposantDTO creer(ComposantCreateDTO dto);
    ComposantDTO getById(Long id);
    List<ComposantDTO> listerParAssemblage(Long assemblageId);
    ComposantDTO modifier(Long id, ComposantUpdateDTO dto);
    void supprimer(Long id);
    ComposantDTO uploaderImage(Long id, MultipartFile file);
}