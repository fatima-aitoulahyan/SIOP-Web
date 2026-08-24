package com.example.backend_siop.tache.entity;

import com.example.backend_siop.common.audit.Auditable;
import com.example.backend_siop.tache.enums.Priorite;
import com.example.backend_siop.tache.enums.StatutTache;
import com.example.backend_siop.tache.enums.TypeTache;
import com.example.backend_siop.ascenseur.entity.Ascenseur;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tache")
@EqualsAndHashCode(callSuper = false, of = "id")
public class Tache extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTache type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTache statut = StatutTache.A_FAIRE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priorite priorite = Priorite.MOYENNE;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_completion")
    private LocalDateTime dateCompletion;

    // L'ascenseur concerné
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ascenseur_id", nullable = false)
    private Ascenseur ascenseur;

    // L'admin qui a créé la tâche
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createur_id")
    private Utilisateur createur;

    // Le responsable de maintenance assigné
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Utilisateur responsable;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tache_technicien",
            joinColumns = @JoinColumn(name = "tache_id"),
            inverseJoinColumns = @JoinColumn(name = "technicien_id")
    )
    private List<Utilisateur> techniciens = new ArrayList<>();


}