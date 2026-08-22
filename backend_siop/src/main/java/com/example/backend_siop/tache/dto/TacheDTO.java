package com.example.backend_siop.tache.dto;

import com.example.backend_siop.tache.enums.Priorite;
import com.example.backend_siop.tache.enums.StatutTache;
import com.example.backend_siop.tache.enums.TypeTache;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List; // ✅ C'EST CETTE LIGNE QUI MANQUAIT !

@Getter
@Setter
public class TacheDTO {
    private Long id;
    private String titre;
    private String description;
    private TypeTache type;
    private StatutTache statut;
    private Priorite priorite;
    private LocalDate dateEcheance;
    private LocalDateTime dateCreation;
    private LocalDateTime dateCompletion;

    // Infos ascenseur
    private Long ascenseurId;
    private String ascenseurNom;
    private String ascenseurSite;
    private String ascenseurClient;

    // Infos responsables
    private Long createurId;
    private String createurNom;
    private Long responsableId;
    private String responsableNom;

    private List<Long> technicienIds;
    private List<String> technicienNoms;
}