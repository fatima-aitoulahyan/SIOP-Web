package com.example.backend_siop.ascenseur.entity;
import com.example.backend_siop.ascenseur.enums.TypeAssemblage;
import com.example.backend_siop.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "assemblage")
@EqualsAndHashCode(callSuper = false, of = "id")
public class Assemblage extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAssemblage type;

    private String fabricant;

    @Column(name = "date_installation")
    private LocalDate dateInstallation;

    @Column(name = "duree_vie_estimee_mois")
    private Integer dureeVieEstimeeMois;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "niveau")
    private Integer niveau;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ascenseur_id", nullable = false)
    private Ascenseur ascenseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Assemblage parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assemblage> sousAssemblages = new ArrayList<>();

    @OneToMany(mappedBy = "assemblage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Composant> composants = new ArrayList<>();
}