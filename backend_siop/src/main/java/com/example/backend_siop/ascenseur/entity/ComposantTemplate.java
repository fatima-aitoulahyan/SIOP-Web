package com.example.backend_siop.ascenseur.entity;

import com.example.backend_siop.ascenseur.enums.TypeComposant;
import com.example.backend_siop.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "composant_template")
@EqualsAndHashCode(callSuper = false, of = "id")
public class ComposantTemplate extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeComposant type;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assemblage_template_id", nullable = false)
    private AssemblageTemplate assemblageTemplate;
}