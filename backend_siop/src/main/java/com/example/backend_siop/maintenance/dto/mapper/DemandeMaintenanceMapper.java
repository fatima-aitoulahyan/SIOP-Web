package com.example.backend_siop.maintenance.dto.mapper;

import com.example.backend_siop.common.dto.PieceJointeAvecUrlDTO;
import com.example.backend_siop.common.entity.PieceJointe;
import com.example.backend_siop.common.enums.TypeEntiteJointe;
import com.example.backend_siop.common.repository.PieceJointeRepository;
import com.example.backend_siop.common.util.FileStorageUtil;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceCreateDTO;
import com.example.backend_siop.maintenance.dto.DemandeMaintenanceDTO;
import com.example.backend_siop.maintenance.entity.DemandeMaintenance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class DemandeMaintenanceMapper {

    @Autowired
    protected PieceJointeRepository pieceJointeRepository;

    @Autowired
    protected FileStorageUtil fileStorageUtil;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "motifRejet", ignore = true)
    @Mapping(target = "ascenseur", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "villeSaisie", ignore = true)
    @Mapping(target = "adresseSaisie", ignore = true)
    public abstract DemandeMaintenance toEntity(DemandeMaintenanceCreateDTO dto);

    @Mapping(target = "ascenseurId", source = "ascenseur.id")
    @Mapping(target = "ascenseurNom", source = "ascenseur.nom")
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(
            target = "clientNom",
            source = "client",
            qualifiedByName = "clientToNom"
    )
    @Mapping(target = "villeSaisie", source = "villeSaisie")
    @Mapping(target = "adresseSaisie", source = "adresseSaisie")
    @Mapping(
            target = "photos",
            source = "id",
            qualifiedByName = "mapPhotos"
    )
    public abstract DemandeMaintenanceDTO toDTO(
            DemandeMaintenance entity
    );

    @Named("clientToNom")
    protected String clientToNom(
            com.example.backend_siop.utilisateur.entity.Client client
    ) {
        if (client == null) {
            return null;
        }

        String prenom = client.getPrenom() != null
                ? client.getPrenom()
                : "";

        String nom = client.getNom() != null
                ? client.getNom()
                : "";

        String nomComplet = (prenom + " " + nom).trim();

        if (nomComplet.isEmpty()
                && client.getNomEntreprise() != null) {
            return client.getNomEntreprise();
        }

        return nomComplet.isEmpty() ? null : nomComplet;
    }

    @Named("mapPhotos")
    protected List<PieceJointeAvecUrlDTO> mapPhotos(Long demandeId) {

        if (demandeId == null) {
            return List.of();
        }

        List<PieceJointe> pieces =
                pieceJointeRepository.findByEntiteTypeAndEntiteId(
                        TypeEntiteJointe.DEMANDE_MAINTENANCE,
                        demandeId
                );

        return pieces.stream()
                .map(p -> new PieceJointeAvecUrlDTO(
                        p.getId(),
                        p.getNomFichier(),
                        fileStorageUtil.getUrlTemporaire(
                                p.getCheminFichier()
                        ),
                        p.getTypeFichier(),
                        p.getDescription()
                ))
                .collect(Collectors.toList());
    }
}