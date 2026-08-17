package com.example.backend_siop.ascenseur.controller;

import com.example.backend_siop.ascenseur.dto.composant.ComposantCreateDTO;
import com.example.backend_siop.ascenseur.dto.composant.ComposantDTO;
import com.example.backend_siop.ascenseur.dto.composant.ComposantUpdateDTO;
import com.example.backend_siop.ascenseur.service.ComposantService;
import com.example.backend_siop.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/composants")
@RequiredArgsConstructor
public class ComposantController {

    private final ComposantService composantService;

    @PostMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE','ADMINISTRATEUR')")
    public ApiResponse<ComposantDTO> creer(@Valid @RequestBody ComposantCreateDTO dto) {
        return ApiResponse.success(composantService.creer(dto));
    }

    @GetMapping("/{id}")
    public ApiResponse<ComposantDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(composantService.getById(id));
    }

    @GetMapping("/assemblage/{assemblageId}")
    public ApiResponse<List<ComposantDTO>> listerParAssemblage(@PathVariable Long assemblageId) {
        return ApiResponse.success(composantService.listerParAssemblage(assemblageId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE','ADMINISTRATEUR')")
    public ApiResponse<ComposantDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody ComposantUpdateDTO dto) {
        return ApiResponse.success(composantService.modifier(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE','ADMINISTRATEUR')")
    public ApiResponse<Void> supprimer(@PathVariable Long id) {
        composantService.supprimer(id);
        return ApiResponse.success(null);
    }
    @PostMapping("/{id}/image")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE','ADMINISTRATEUR')")
    public ApiResponse<ComposantDTO> uploaderImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(composantService.uploaderImage(id, file));
    }
}