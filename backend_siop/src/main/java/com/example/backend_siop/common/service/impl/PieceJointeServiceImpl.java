package com.example.backend_siop.common.service.impl;

import com.example.backend_siop.common.dto.PieceJointeAvecUrlDTO;
import com.example.backend_siop.common.entity.PieceJointe;
import com.example.backend_siop.common.enums.TypeEntiteJointe;
import com.example.backend_siop.common.enums.TypeFichier;
import com.example.backend_siop.common.exception.BusinessRuleException;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.common.repository.PieceJointeRepository;
import com.example.backend_siop.common.service.PieceJointeService;
import com.example.backend_siop.common.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PieceJointeServiceImpl implements PieceJointeService {

  private final PieceJointeRepository pieceJointeRepository;
  private final FileStorageUtil fileStorageUtil;

  private static final List<String> MIME_IMAGE = List.of("image/jpeg", "image/png", "image/webp");
  private static final List<String> MIME_PDF = List.of("application/pdf");
  private static final List<String> MIME_AUDIO = List.of(
    "audio/mpeg", "audio/mp4", "audio/wav", "audio/aac",
    "audio/webm", "audio/ogg", "audio/opus"
  );

  @Override
  @Transactional
  public PieceJointe ajouter(MultipartFile fichier, TypeEntiteJointe entiteType, Long entiteId, String description) {
    if (fichier == null || fichier.isEmpty()) {
      throw new BusinessRuleException("Le fichier est vide.");
    }

    TypeFichier type = determinerType(fichier.getContentType());

    String cheminRelatif = fileStorageUtil.store(
      fichier,
      entiteType.name().toLowerCase() + "/" + entiteId
    );

    PieceJointe piece = new PieceJointe();
    piece.setNomFichier(fichier.getOriginalFilename());
    piece.setCheminFichier(cheminRelatif);
    piece.setMimeType(fichier.getContentType());
    piece.setTailleOctets(fichier.getSize());
    piece.setTypeFichier(type);
    piece.setEntiteType(entiteType);
    piece.setEntiteId(entiteId);
    piece.setDescription(description);

    return pieceJointeRepository.save(piece);
  }

  @Override
  public List<PieceJointeAvecUrlDTO> lister(TypeEntiteJointe entiteType, Long entiteId) {
    List<PieceJointe> pieces = pieceJointeRepository.findByEntiteTypeAndEntiteId(entiteType, entiteId);

    return pieces.stream()
      .map(piece -> new PieceJointeAvecUrlDTO(
        piece.getId(),
        piece.getNomFichier(),
        fileStorageUtil.getUrlTemporaire(piece.getCheminFichier()),
        piece.getTypeFichier(),
        piece.getDescription()
      ))
      .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void supprimer(Long pieceJointeId) {
    PieceJointe piece = pieceJointeRepository.findById(pieceJointeId)
      .orElseThrow(() -> new ResourceNotFoundException("Pièce jointe introuvable"));

    fileStorageUtil.delete(piece.getCheminFichier());
    pieceJointeRepository.delete(piece);
  }

  private TypeFichier determinerType(String mimeType) {
    if (mimeType == null) return TypeFichier.AUTRE;

    String typeBase = mimeType.split(";")[0].trim();

    if (MIME_IMAGE.contains(typeBase)) return TypeFichier.IMAGE;
    if (MIME_PDF.contains(typeBase)) return TypeFichier.PDF;
    if (MIME_AUDIO.contains(typeBase)) return TypeFichier.AUTRE;
    return TypeFichier.AUTRE;
  }
}
