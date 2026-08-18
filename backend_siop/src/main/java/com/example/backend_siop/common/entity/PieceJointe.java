package com.example.backend_siop.common.entity;

import com.example.backend_siop.common.audit.Auditable;
import com.example.backend_siop.common.enums.TypeEntiteJointe;
import com.example.backend_siop.common.enums.TypeFichier;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "piece_jointe", indexes = {
        @Index(name = "idx_piece_jointe_entite", columnList = "entite_type, entite_id")
})
@EqualsAndHashCode(callSuper = false, of = "id")
public class PieceJointe extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "chemin_fichier", nullable = false)
    private String cheminFichier;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "taille_octets")
    private Long tailleOctets;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_fichier", nullable = false)
    private TypeFichier typeFichier;

    @Enumerated(EnumType.STRING)
    @Column(name = "entite_type", nullable = false)
    private TypeEntiteJointe entiteType;

    @Column(name = "entite_id", nullable = false)
    private Long entiteId;

    private String description;
}