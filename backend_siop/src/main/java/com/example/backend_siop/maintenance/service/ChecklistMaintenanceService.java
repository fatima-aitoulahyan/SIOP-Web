package com.example.backend_siop.maintenance.service;

import com.example.backend_siop.maintenance.dto.ChecklistMaintenanceDTO;
import com.example.backend_siop.maintenance.dto.ClotureChecklistDTO;
import com.example.backend_siop.maintenance.dto.ItemCheckListUpdateDTO;
import com.example.backend_siop.utilisateur.entity.Technicien;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.example.backend_siop.maintenance.dto.ItemCheckListDTO;

public interface ChecklistMaintenanceService {

    ChecklistMaintenanceDTO getDetail(Long id);

    ChecklistMaintenanceDTO demarrer(Long id, Technicien technicien);

    ChecklistMaintenanceDTO cocherItem(Long itemId, ItemCheckListUpdateDTO dto);

    ChecklistMaintenanceDTO cloturer(Long id, ClotureChecklistDTO dto);

    ChecklistMaintenanceDTO getDetailParBonTravail(Long bonTravailId);

    List<ChecklistMaintenanceDTO> getRapportsAValider();

    ItemCheckListDTO ajouterPhotoItem(Long itemId, MultipartFile file);
}