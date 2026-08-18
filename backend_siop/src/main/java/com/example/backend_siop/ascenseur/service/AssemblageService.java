package com.example.backend_siop.ascenseur.service;

import com.example.backend_siop.ascenseur.dto.Assemblage.AssemblageCreateDTO;
import com.example.backend_siop.ascenseur.dto.Assemblage.AssemblageDTO;
import com.example.backend_siop.ascenseur.dto.Assemblage.AssemblageTreeDTO;
import com.example.backend_siop.ascenseur.dto.Assemblage.AssemblageUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssemblageService {

    AssemblageDTO creer(AssemblageCreateDTO dto);

    AssemblageDTO modifier(Long id, AssemblageUpdateDTO dto);

    void supprimer(Long id);

    AssemblageDTO getById(Long id);

    List<AssemblageDTO> listerTous();

    List<AssemblageDTO> listerParParent(Long parentId);

    List<AssemblageDTO> listerParAscenseur(Long ascenseurId);

    List<AssemblageTreeDTO> listerArbreParAscenseur(Long ascenseurId);
    AssemblageDTO uploaderImage(Long id, MultipartFile file);
}