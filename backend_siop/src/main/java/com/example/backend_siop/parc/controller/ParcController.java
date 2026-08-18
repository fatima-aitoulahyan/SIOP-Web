package com.example.backend_siop.parc.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.parc.dto.ParcCreateDTO;
import com.example.backend_siop.parc.dto.ParcDTO;
import com.example.backend_siop.parc.dto.ParcDetailDTO;
import com.example.backend_siop.parc.service.ParcService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/parcs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE')")
public class ParcController {

    private final ParcService parcService;

    @PostMapping
    public ResponseEntity<ApiResponse<ParcDTO>> creer(@Valid @RequestBody ParcCreateDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(parcService.creer(dto)));
    }

    @GetMapping
    public ApiResponse<List<ParcDTO>> lister() {
        return ApiResponse.success(parcService.lister());
    }

    @GetMapping("/{id}")
    public ApiResponse<ParcDetailDTO> getDetail(@PathVariable Long id) {
        return ApiResponse.success(parcService.getDetail(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<ParcDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody ParcCreateDTO dto) {
        return ApiResponse.success(parcService.modifier(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        parcService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{parcId}/techniciens/{technicienId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE')")
    public ApiResponse<ParcDetailDTO> affecterTechnicien(
            @PathVariable Long parcId,
            @PathVariable Long technicienId) {
        return ApiResponse.success(parcService.affecterTechnicien(parcId, technicienId));
    }

    @DeleteMapping("/{parcId}/techniciens/{technicienId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE')")
    public ApiResponse<ParcDetailDTO> retirerTechnicien(
            @PathVariable Long parcId,
            @PathVariable Long technicienId) {
        return ApiResponse.success(parcService.retirerTechnicien(parcId, technicienId));
    }
}
