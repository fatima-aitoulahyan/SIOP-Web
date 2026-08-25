package com.example.backend_siop.maintenance.service;

import com.example.backend_siop.maintenance.dto.EvaluationAscenseurDTO;
import com.example.backend_siop.maintenance.dto.EvaluationAscenseurSoumissionDto;
import com.example.backend_siop.maintenance.dto.EvaluationAscenseurValidationDto;

import java.util.List;

public interface EvaluationAscenseurService {

    EvaluationAscenseurDTO creerBrouillon(Long bonTravailId, Long technicienId);

    EvaluationAscenseurDTO soumettre(Long evaluationId, EvaluationAscenseurSoumissionDto dto, Long technicienId);

    EvaluationAscenseurDTO valider(Long evaluationId, Long responsableId, EvaluationAscenseurValidationDto dto);
    EvaluationAscenseurDTO getById(Long evaluationId);

    EvaluationAscenseurDTO getByBonTravailId(Long bonTravailId);

    List<EvaluationAscenseurDTO> getEnAttenteValidation();

    List<EvaluationAscenseurDTO> listerParTechnicien(Long technicienId);

    List<EvaluationAscenseurDTO> listerParClient(Long clientId);
}