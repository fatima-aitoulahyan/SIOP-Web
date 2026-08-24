package com.example.backend_siop.maintenance.controller;

import com.example.backend_siop.common.dto.ApiResponse;
import com.example.backend_siop.maintenance.dto.ChecklistMaintenanceDTO;
import com.example.backend_siop.maintenance.dto.ClotureChecklistDTO;
import com.example.backend_siop.maintenance.dto.ItemCheckListDTO;
import com.example.backend_siop.maintenance.dto.ItemCheckListUpdateDTO;
import com.example.backend_siop.maintenance.service.ChecklistMaintenanceService;
import com.example.backend_siop.utilisateur.entity.Technicien;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
public class ChecklistMaintenanceController {

    private final ChecklistMaintenanceService checklistService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TECHNICIEN', 'RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<ChecklistMaintenanceDTO> getDetail(@PathVariable Long id) {
        return ApiResponse.success(checklistService.getDetail(id));
    }

    @GetMapping("/rapports-a-valider")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<List<ChecklistMaintenanceDTO>> getRapportsAValider() {
        return ApiResponse.success(checklistService.getRapportsAValider());
    }

    @PatchMapping("/{id}/demarrer")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ApiResponse<ChecklistMaintenanceDTO> demarrer(
            @PathVariable Long id,
            @AuthenticationPrincipal Technicien technicien) {
        return ApiResponse.success(checklistService.demarrer(id, technicien));
    }

    @PostMapping("/items/{itemId}/photos")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ApiResponse<ItemCheckListDTO> ajouterPhotoItem(
            @PathVariable Long itemId,
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(checklistService.ajouterPhotoItem(itemId, file));
    }

    @PatchMapping("/items/{itemId}")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ApiResponse<ChecklistMaintenanceDTO> cocherItem(
            @PathVariable Long itemId,
            @Valid @RequestBody ItemCheckListUpdateDTO dto) {
        return ApiResponse.success(checklistService.cocherItem(itemId, dto));
    }

    @PatchMapping("/{id}/cloturer")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ApiResponse<ChecklistMaintenanceDTO> cloturer(
            @PathVariable Long id,
            @Valid @RequestBody ClotureChecklistDTO dto) {
        return ApiResponse.success(checklistService.cloturer(id, dto));
    }

    @GetMapping("/par-bon-travail/{bonTravailId}")
    @PreAuthorize("hasAnyRole('TECHNICIEN', 'RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR')")
    public ApiResponse<ChecklistMaintenanceDTO> getDetailParBonTravail(
            @PathVariable Long bonTravailId) {
        return ApiResponse.success(checklistService.getDetailParBonTravail(bonTravailId));
    }
}