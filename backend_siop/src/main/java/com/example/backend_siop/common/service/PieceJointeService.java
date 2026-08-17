package com.example.backend_siop.common.service;

import com.example.backend_siop.common.entity.PieceJointe;
import com.example.backend_siop.common.enums.TypeEntiteJointe;
import com.example.backend_siop.common.enums.TypeFichier;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PieceJointeService {

    PieceJointe ajouter(MultipartFile fichier, TypeEntiteJointe entiteType, Long entiteId, String description);

    List<PieceJointe> lister(TypeEntiteJointe entiteType, Long entiteId);

    void supprimer(Long pieceJointeId);
}