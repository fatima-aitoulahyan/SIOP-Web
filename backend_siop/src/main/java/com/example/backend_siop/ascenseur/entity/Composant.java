package com.example.backend_siop.ascenseur.entity;

import com.example.backend_siop.ascenseur.enums.TypeComposant;
import com.example.backend_siop.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "composant")
@EqualsAndHashCode(callSuper = false, of = "id")
public class Composant extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeComposant type;

    private String fabricant;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "date_installation")
    private LocalDate dateInstallation;

    @Column(name = "duree_vie_estimee_mois")
    private Integer dureeVieEstimeeMois;

    private boolean actif = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assemblage_id", nullable = false)
    private Assemblage assemblage;

}