package com.example.backend_siop.common;

import com.example.backend_siop.common.entity.PieceJointe;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.common.repository.PieceJointeRepository;
import com.example.backend_siop.common.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("/api/fichiers")
@RequiredArgsConstructor
public class FichierController {

    private final PieceJointeRepository pieceJointeRepository;
    private final FileStorageUtil fileStorageUtil;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InputStreamResource> telecharger(@PathVariable Long id) {
        PieceJointe piece = pieceJointeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fichier introuvable"));

        InputStream is = fileStorageUtil.download(piece.getCheminFichier());

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (piece.getMimeType() != null) {
            try {
                mediaType = MediaType.parseMediaType(piece.getMimeType());
            } catch (Exception ignored) {
            }
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + piece.getNomFichier() + "\"")
                .body(new InputStreamResource(is));
    }
}