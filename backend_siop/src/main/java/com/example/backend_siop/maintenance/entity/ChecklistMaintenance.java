package com.example.backend_siop.maintenance.entity;

import com.example.backend_siop.ascenseur.entity.Ascenseur;
import com.example.backend_siop.common.audit.Auditable;
import com.example.backend_siop.utilisateur.entity.Technicien;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "checklist_maintenance",
       uniqueConstraints = @UniqueConstraint(columnNames = {"ascenseur_id", "mois", "annee"}))
@EqualsAndHashCode(callSuper = false, of = "id")
public class ChecklistMaintenance extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer mois;

    @Column(nullable = false)
    private Integer annee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ascenseur_id", nullable = false)
    private Ascenseur ascenseur;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bon_travail_id", nullable = true, unique = true)
    private BonTravail bonTravail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technicien_id")
    private Technicien technicien;

    private LocalTime heureArrivee;

    private LocalTime heureDepart;

    @Column(name = "est_maintenance", nullable = false)
    private boolean estMaintenance = false;

    @Column(name = "est_depannage", nullable = false)
    private boolean estDepannage = false;

    @Column(name = "est_travaux", nullable = false)
    private boolean estTravaux = false;

    @Column(name = "bilan_intervention", columnDefinition = "TEXT")
    private String bilanIntervention;

    @OneToMany(mappedBy = "checklistMaintenance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCheckList> items = new ArrayList<>();
}