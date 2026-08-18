package com.example.backend_siop.utilisateur.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.utilisateur.dto.TechnicienResumeDTO;
import com.example.backend_siop.utilisateur.dto.mapper.TechnicienMapper;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.repository.TechnicienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/techniciens")
@RequiredArgsConstructor
public class TechnicienController {

    private final TechnicienRepository technicienRepository;
    private final TechnicienMapper mapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE')")
    public ApiResponse<List<TechnicienResumeDTO>> lister() {
        return ApiResponse.success(
                technicienRepository.findByActifTrue()
                        .stream()
                        .map(mapper::toResumeDTO)
                        .toList()
        );
    }

    @GetMapping("/parc/{parcId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE')")
    public ApiResponse<List<TechnicienResumeDTO>> listerParParc(@PathVariable Long parcId) {
        return ApiResponse.success(
                technicienRepository.findByParcs_Id(parcId)
                        .stream()
                        .filter(Technicien::isActif)
                        .map(mapper::toResumeDTO)
                        .toList()
        );
    }
}