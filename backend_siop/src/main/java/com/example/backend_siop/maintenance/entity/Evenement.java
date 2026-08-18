package com.example.backend_siop.maintenance.entity;

import com.example.backend_siop.common.audit.Auditable;
import com.example.backend_siop.maintenance.enums.TypeEvenement;
import com.example.backend_siop.utilisateur.entity.Technicien;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "evenement")
@EqualsAndHashCode(callSuper = false, of = "id")
public class Evenement extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEvenement type; // REUNION, CONGE, FORMATION, AUTRE

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @Column(name = "lieu")
    private String lieu;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cree_par_id", nullable = false)
    private Utilisateur creePar;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "evenement_technicien",
            joinColumns = @JoinColumn(name = "evenement_id"),
            inverseJoinColumns = @JoinColumn(name = "technicien_id")
    )
    private List<Technicien> participants = new ArrayList<>();
}