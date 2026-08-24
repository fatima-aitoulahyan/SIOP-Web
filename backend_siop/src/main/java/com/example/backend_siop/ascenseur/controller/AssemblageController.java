package com.example.backend_siop.ascenseur.controller;

import com.example.backend_siop.ascenseur.dto.Assemblage.AssemblageCreateDTO;
import com.example.backend_siop.ascenseur.dto.Assemblage.AssemblageDTO;
import com.example.backend_siop.ascenseur.dto.Assemblage.AssemblageTreeDTO;
import com.example.backend_siop.ascenseur.dto.Assemblage.AssemblageUpdateDTO;
import com.example.backend_siop.ascenseur.service.AssemblageService;
import com.example.backend_siop.ascenseur.service.impl.ArborescenceService;
import com.example.backend_siop.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/assemblages")
@RequiredArgsConstructor
public class AssemblageController {

    private final AssemblageService assemblageService;
    private final ArborescenceService arborescenceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE' ,'ADMINISTRATEUR')")
    public ApiResponse<AssemblageDTO> creer(@Valid @RequestBody AssemblageCreateDTO dto) {
        return ApiResponse.success(assemblageService.creer(dto));
    }

    @GetMapping("/{id}")
    public ApiResponse<AssemblageDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(assemblageService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<List<AssemblageDTO>> listerTous() {
        return ApiResponse.success(assemblageService.listerTous());
    }

    @GetMapping("/parent/{parentId}")
    public ApiResponse<List<AssemblageDTO>> listerParParent(@PathVariable Long parentId) {
        return ApiResponse.success(assemblageService.listerParParent(parentId));
    }

    @GetMapping("/ascenseur/{ascenseurId}")
    public ApiResponse<List<AssemblageDTO>> listerParAscenseur(@PathVariable Long ascenseurId) {
        return ApiResponse.success(assemblageService.listerParAscenseur(ascenseurId));
    }

    @GetMapping("/ascenseur/{ascenseurId}/arbre")
    public ApiResponse<List<AssemblageTreeDTO>> arbreParAscenseur(@PathVariable Long ascenseurId) {
        return ApiResponse.success(assemblageService.listerArbreParAscenseur(ascenseurId));
    }
    @PostMapping("/ascenseur/{ascenseurId}/generer")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE','ADMINISTRATEUR')")
    public ApiResponse<Void> genererArborescence(@PathVariable Long ascenseurId) {
        arborescenceService.genererArborescencePourAscenseur(ascenseurId);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE','ADMINISTRATEUR')")
    public ApiResponse<AssemblageDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody AssemblageUpdateDTO dto) {
        return ApiResponse.success(assemblageService.modifier(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE','ADMINISTRATEUR')")
    public ApiResponse<Void> supprimer(@PathVariable Long id) {
        assemblageService.supprimer(id);
        return ApiResponse.success(null);
    }
    @PostMapping("/{id}/image")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE','ADMINISTRATEUR')")
    public ApiResponse<AssemblageDTO> uploaderImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(assemblageService.uploaderImage(id, file));
    }
}