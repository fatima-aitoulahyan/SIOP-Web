package com.example.backend_siop.maintenance.service.impl;

import com.example.backend_siop.common.dto.PieceJointeAvecUrlDTO;
import com.example.backend_siop.common.entity.PieceJointe;
import com.example.backend_siop.common.enums.TypeEntiteJointe;
import com.example.backend_siop.common.exception.BusinessRuleException;
import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.common.repository.PieceJointeRepository;
import com.example.backend_siop.common.util.FileStorageUtil;
import com.example.backend_siop.maintenance.dto.ChecklistMaintenanceDTO;
import com.example.backend_siop.maintenance.dto.ClotureChecklistDTO;
import com.example.backend_siop.maintenance.dto.ItemCheckListDTO;
import com.example.backend_siop.maintenance.dto.ItemCheckListUpdateDTO;
import com.example.backend_siop.maintenance.dto.mapper.ChecklistMaintenanceMapper;
import com.example.backend_siop.maintenance.entity.ChecklistMaintenance;
import com.example.backend_siop.maintenance.entity.ItemCheckList;
import com.example.backend_siop.maintenance.enums.StatutBonTravail;
import com.example.backend_siop.maintenance.enums.StatutDemande;
import com.example.backend_siop.maintenance.enums.StatutItem;
import com.example.backend_siop.maintenance.repository.BonTravailRepository;
import com.example.backend_siop.maintenance.repository.ChecklistMaintenanceRepository;
import com.example.backend_siop.maintenance.repository.ItemCheckListRepository;
import com.example.backend_siop.maintenance.entity.BonTravail;
import com.example.backend_siop.maintenance.service.ChecklistMaintenanceService;
import com.example.backend_siop.utilisateur.entity.Technicien;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.backend_siop.maintenance.dto.ItemCheckListDTO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistMaintenanceServiceImpl implements ChecklistMaintenanceService {

    private final ChecklistMaintenanceRepository checklistRepository;
    private final ItemCheckListRepository itemCheckListRepository;
    private final BonTravailRepository bonTravailRepository;
    private final PieceJointeRepository pieceJointeRepository;
    private final FileStorageUtil fileStorageUtil;
    private final ChecklistMaintenanceMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public ChecklistMaintenanceDTO getDetail(Long id) {
        ChecklistMaintenance checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist introuvable"));
        return toDTOAvecItems(checklist);
    }

    @Override
    @Transactional
    public ChecklistMaintenanceDTO demarrer(Long id, Technicien technicien) {
        ChecklistMaintenance checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist introuvable"));

        checklist.setTechnicien(technicien);
        checklist.setHeureArrivee(LocalTime.now());

        if (checklist.getBonTravail() != null) {
            BonTravail bonTravail = checklist.getBonTravail();
            bonTravail.setStatut(StatutBonTravail.EN_COURS);
            bonTravailRepository.save(bonTravail);
        }

        return toDTOAvecItems(checklistRepository.save(checklist));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChecklistMaintenanceDTO> getRapportsAValider() {
        List<ChecklistMaintenance> checklists = checklistRepository.findByHeureDepartIsNotNullOrderByHeureDepartDesc();
        return checklists.stream()
                .map(this::toDTOAvecItems)
                .toList();
    }

    @Override
    @Transactional
    public ChecklistMaintenanceDTO cocherItem(Long itemId, ItemCheckListUpdateDTO dto) {
        ItemCheckList item = itemCheckListRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item introuvable"));

        if (dto.getStatut() == StatutItem.ANOMALIE_DETECTEE && dto.getGravite() == null) {
            throw new BusinessRuleException(
                    "La gravité est obligatoire lorsqu'une anomalie est détectée.");
        }

        item.setStatut(dto.getStatut());
        item.setGravite(dto.getStatut() == StatutItem.ANOMALIE_DETECTEE ? dto.getGravite() : null);
        item.setRemarque(dto.getRemarque());
        itemCheckListRepository.save(item);

        return toDTOAvecItems(item.getChecklistMaintenance());
    }

    @Override
    @Transactional
    public ChecklistMaintenanceDTO cloturer(Long id, ClotureChecklistDTO dto) {
        ChecklistMaintenance checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist introuvable"));

        List<ItemCheckList> items = itemCheckListRepository
                .findByChecklistMaintenanceIdOrderByOrdreAsc(id);

        boolean resteNonVerifie = items.stream()
                .anyMatch(i -> i.getStatut() == StatutItem.NON_VERIFIE);
        if (resteNonVerifie) {
            throw new BusinessRuleException(
                    "Tous les items doivent être vérifiés avant de clôturer la checklist.");
        }

        checklist.setHeureDepart(LocalTime.now());
        checklist.setBilanIntervention(dto.getBilanIntervention());
        checklist.setEstMaintenance(dto.isEstMaintenance());
        checklist.setEstDepannage(dto.isEstDepannage());
        checklist.setEstTravaux(dto.isEstTravaux());

        if (checklist.getBonTravail() != null) {
            BonTravail bonTravail = checklist.getBonTravail();
            bonTravail.setStatut(StatutBonTravail.TERMINE);
            if (bonTravail.getDemandeMaintenance() != null) {
                bonTravail.getDemandeMaintenance().setStatut(StatutDemande.RESOLUE);
                bonTravail.getDemandeMaintenance().setDateResolution(LocalDateTime.now());
            }
            bonTravailRepository.save(bonTravail);
        }

        return toDTOAvecItems(checklistRepository.save(checklist));
    }

    private ChecklistMaintenanceDTO toDTOAvecItems(ChecklistMaintenance checklist) {
        ChecklistMaintenanceDTO dto = mapper.toDTO(checklist);

        List<ItemCheckList> items = itemCheckListRepository
                .findByChecklistMaintenanceIdOrderByOrdreAsc(checklist.getId());

        List<ItemCheckListDTO> itemsDTO = items.stream()
                .map(item -> {
                    ItemCheckListDTO itemDTO = mapper.toItemDTO(item);
                    itemDTO.setPiecesJointes(recupererPieces(item.getId()));
                    return itemDTO;
                })
                .toList();

        dto.setItems(itemsDTO);
        return dto;
    }

    @Override
    @Transactional
    public ItemCheckListDTO ajouterPhotoItem(Long itemId, MultipartFile file) {
        ItemCheckList item = itemCheckListRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item introuvable"));

        try {
            String chemin = fileStorageUtil.store(file, "item_checklist/" + itemId);

            com.example.backend_siop.common.enums.TypeFichier typeFichier = com.example.backend_siop.common.enums.TypeFichier.DOCUMENT;
            String contentType = file.getContentType();
            if (contentType != null) {
                if (contentType.startsWith("image/")) {
                    typeFichier = com.example.backend_siop.common.enums.TypeFichier.IMAGE;
                } else if (contentType.startsWith("audio/")) {
                    typeFichier = com.example.backend_siop.common.enums.TypeFichier.AUDIO;
                } else if (contentType.startsWith("video/")) {
                    typeFichier = com.example.backend_siop.common.enums.TypeFichier.VIDEO;
                }
            }

            PieceJointe pj = new PieceJointe();
            pj.setEntiteType(TypeEntiteJointe.ITEM_CHECKLIST);
            pj.setEntiteId(itemId);
            pj.setNomFichier(file.getOriginalFilename());
            pj.setCheminFichier(chemin);
            pj.setTypeFichier(typeFichier);
            pj.setTailleOctets(file.getSize());

            pieceJointeRepository.save(pj);

            return mapper.toItemDTO(item);

        } catch (Exception e) {
            throw new BusinessRuleException("Erreur lors de l'upload de la photo : " + e.getMessage());
        }
    }


    private List<PieceJointeAvecUrlDTO> recupererPieces(Long itemId) {
        List<PieceJointe> pieces = pieceJointeRepository
                .findByEntiteTypeAndEntiteId(TypeEntiteJointe.ITEM_CHECKLIST, itemId);
        return pieces.stream()
                .map(p -> new PieceJointeAvecUrlDTO(
                        p.getId(),
                        p.getNomFichier(),
                        fileStorageUtil.getUrlTemporaire(p.getCheminFichier()),
                        p.getTypeFichier(),
                        p.getDescription()
                ))
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public ChecklistMaintenanceDTO getDetailParBonTravail(Long bonTravailId) {
        ChecklistMaintenance checklist = checklistRepository.findByBonTravailId(bonTravailId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Aucune checklist trouvée pour ce bon de travail"));
        return toDTOAvecItems(checklist);
    }
}
