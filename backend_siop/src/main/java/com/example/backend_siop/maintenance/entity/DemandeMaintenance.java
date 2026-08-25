package com.example.backend_siop.maintenance.entity;

import com.example.backend_siop.ascenseur.entity.Ascenseur;
import com.example.backend_siop.ascenseur.entity.Site;
import com.example.backend_siop.common.audit.Auditable;
import com.example.backend_siop.maintenance.enums.PrioriteDemande;
import com.example.backend_siop.maintenance.enums.StatutDemande;
import com.example.backend_siop.maintenance.enums.TypeDemande;
import com.example.backend_siop.utilisateur.entity.Client;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "demande_maintenance")
@EqualsAndHashCode(callSuper = false, of = "id")
public class DemandeMaintenance extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDemande typeDemande;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioriteDemande priorite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDemande statut;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_souhaitee")
    private LocalDate dateSouhaitee;

    @Column(name = "date_resolution")
    private LocalDateTime dateResolution;

    @Column(name = "motif_rejet", columnDefinition = "TEXT")
    private String motifRejet;

    // Nullable : absent pour type=EVALUATION, l'ascenseur n'existe pas encore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "ascenseur_id", nullable = true)
    private Ascenseur ascenseur;

    // ⚠️ Rendre nullable = true pour permettre les demandes sans client (intégration n8n)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    // Nullable : absent pour type=EVALUATION, le site n'existe pas encore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "site_id", nullable = true)
    private Site site;

    // Renseignés uniquement pour type=EVALUATION, tant que le Site n'est pas encore créé
    @Column(name = "ville_saisie")
    private String villeSaisie;

    @Column(name = "adresse_saisie")
    private String adresseSaisie;
}