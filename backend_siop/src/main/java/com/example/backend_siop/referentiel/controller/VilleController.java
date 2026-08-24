package com.example.backend_siop.referentiel.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.referentiel.dto.VilleCreateDTO;
import com.example.backend_siop.referentiel.dto.VilleResponseDTO;
import com.example.backend_siop.referentiel.service.VilleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villes")
@RequiredArgsConstructor
public class VilleController {

    private final VilleService villeService;

    @GetMapping
    public ApiResponse<List<VilleResponseDTO>> lister() {
        return ApiResponse.success(villeService.listerToutes());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<VilleResponseDTO> creer(@Valid @RequestBody VilleCreateDTO dto) {
        return ApiResponse.success(villeService.creer(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<VilleResponseDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody VilleCreateDTO dto) {
        return ApiResponse.success(villeService.modifier(id, dto));
    }
}