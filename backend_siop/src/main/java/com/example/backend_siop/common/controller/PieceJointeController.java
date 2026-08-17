package com.example.backend_siop.common.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.common.entity.PieceJointe;
import com.example.backend_siop.common.enums.TypeEntiteJointe;
import com.example.backend_siop.common.service.PieceJointeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/pieces-jointes")
@RequiredArgsConstructor
public class PieceJointeController {

    private final PieceJointeService pieceJointeService;

    @PostMapping
    public ApiResponse<PieceJointe> ajouter(
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam TypeEntiteJointe entiteType,
            @RequestParam Long entiteId,
            @RequestParam(required = false) String description) {
        return ApiResponse.success(pieceJointeService.ajouter(fichier, entiteType, entiteId, description));
    }

    @GetMapping
    public ApiResponse<List<PieceJointe>> lister(
            @RequestParam TypeEntiteJointe entiteType,
            @RequestParam Long entiteId) {
        return ApiResponse.success(pieceJointeService.lister(entiteType, entiteId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> supprimer(@PathVariable Long id) {
        pieceJointeService.supprimer(id);
        return ApiResponse.success(null);
    }
}