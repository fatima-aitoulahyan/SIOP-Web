package com.example.backend_siop.maintenance.dto;

import com.example.backend_siop.maintenance.enums.PrioriteDemande;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BonTravailCreateDTO {

    private Long demandeMaintenanceId;
    private Long ascenseurId;
    private Long parcId;
    private Long siteId;

    @NotNull(message = "Le technicien responsable est obligatoire")
    private Long technicienResponsableId;

    private List<Long> technicienIdsRenfort;

    @NotNull(message = "La date d'intervention est obligatoire")
    private LocalDateTime dateInterventionPrevue;

    @NotNull(message = "La durée estimée est obligatoire")
    private Integer dureeEstimeeMinutes;

    private PrioriteDemande priorite;
    private String description;

    private boolean visitePreventive = false;
}