package com.example.backend_siop.parc.service;

import java.util.List;

import com.example.backend_siop.parc.dto.ParcCreateDTO;
import com.example.backend_siop.parc.dto.ParcDTO;
import com.example.backend_siop.parc.dto.ParcDetailDTO;

public interface ParcService {

    ParcDTO creer(ParcCreateDTO dto);

    List<ParcDTO> lister();

    ParcDetailDTO getDetail(Long id);

    ParcDTO modifier(Long id, ParcCreateDTO dto);

    void supprimer(Long id);

    ParcDetailDTO affecterTechnicien(Long parcId, Long technicienId);

    ParcDetailDTO retirerTechnicien(Long parcId, Long technicienId);
}