/**
 * Ce DTO sert à répondre à la question "pourquoi ce technicien n'est-il pas libre" — 
 * il pointe vers le bon de travail précis qui cause le conflit, avec l'ascenseur 
 * et la date concernés, pour que le Responsable comprenne immédiatement 
 * la situation sans chercher.
 */

package com.example.backend_siop.maintenance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConflitTechnicienDTO {
    private Long technicienId;
    private String technicienNom;
    private Long bonTravailConflitId;
    private String ascenseurNom;
    private String dateInterventionPrevue;
}