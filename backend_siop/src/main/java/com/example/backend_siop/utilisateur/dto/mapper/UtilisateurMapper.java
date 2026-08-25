package com.example.backend_siop.utilisateur.dto.mapper;

import com.example.backend_siop.utilisateur.dto.UtilisateurResponseDTO;
import com.example.backend_siop.utilisateur.entity.Client;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UtilisateurMapper {

    public UtilisateurResponseDTO toDTO(Utilisateur u) {
        UtilisateurResponseDTO.UtilisateurResponseDTOBuilder builder = UtilisateurResponseDTO.builder()
                .id(u.getId())
                .email(u.getEmail())
                .telephone(u.getTelephone())
                .nom(u.getNom())
                .prenom(u.getPrenom())
                .nomEntreprise(u.getNomEntreprise())
                .actif(u.isActif())
                .type(u.getType())
                .createdAt(u.getCreatedAt());

        if (u instanceof Client client) {
            builder.adresse(client.getAdresse());
        } else if (u instanceof Technicien technicien) {
            builder.specialite(technicien.getSpecialite());

            // 🔥 Ajout de la liste des parcs pour les techniciens (deploy-dokploy)
            if (technicien.getParcs() != null) {
                List<Long> parcIds = technicien.getParcs()
                        .stream()
                        .map(parc -> parc.getId())
                        .toList();
                builder.parcIds(parcIds);
            }
        }

        return builder.build();
    }
}