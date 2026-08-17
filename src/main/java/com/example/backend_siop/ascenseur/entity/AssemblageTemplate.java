package com.example.backend_siop.ascenseur.entity;

import com.example.backend_siop.ascenseur.enums.TypeAscenseur;
import com.example.backend_siop.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "assemblage_template")
@EqualsAndHashCode(callSuper = false, of = "id")
public class AssemblageTemplate extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private Integer niveau;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_ascenseur", nullable = false)
    private TypeAscenseur typeAscenseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private AssemblageTemplate parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssemblageTemplate> sousAssemblages = new ArrayList<>();

    @OneToMany(mappedBy = "assemblageTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComposantTemplate> composants = new ArrayList<>();
}